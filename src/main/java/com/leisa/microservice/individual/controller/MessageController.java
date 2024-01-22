package com.leisa.microservice.individual.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leisa.microservice.individual.Service.UserService;
import com.leisa.microservice.individual.model.QueueMapping;
import com.leisa.microservice.individual.model.QueueMappingRequest;
import com.leisa.microservice.individual.model.User;
import com.leisa.microservice.individual.repository.QueueMappingRepository;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@RestController
@RequestMapping("/publish")
public class MessageController {
    private final BCryptPasswordEncoder passwordEncoder;
    private final QueueMappingRepository queueMappingRepository;
    private final WebClient webClient;
    private final JSONObject config;
    private final ConnectionFactory rabbitConnectionFactory;
    private static final Logger logger = Logger.getLogger(MessageController.class.getName());

    public MessageController(UserService userService, QueueMappingRepository queueMappingRepository) throws IOException {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.queueMappingRepository = queueMappingRepository;
        this.webClient = WebClient.builder()
                .baseUrl("https://messagevalidatorapi.azurewebsites.net")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.json");
        if (inputStream == null) {
            throw new IOException("config.json file not found in resources");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String configJSON = reader.lines().collect(Collectors.joining("\n"));
            this.config = new JSONObject(configJSON);
        }

        this.rabbitConnectionFactory = new ConnectionFactory();
        this.rabbitConnectionFactory.setHost(config.getString("brokerHost"));
        this.rabbitConnectionFactory.setPort(config.getInt("brokerPort"));
    }

    @PostMapping("/individual")
    public Mono<ResponseEntity<?>> publish(@RequestBody QueueMappingRequest queueMappingRequest,
            HttpServletRequest request) {
        return Mono.just(request)
                .flatMap(this::extractCredentials)
                .flatMap(credentials -> validateAndPublish(queueMappingRequest, credentials))
                .onErrorResume(e -> {
                    logger.warning("Error during processing: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
                });
    }

    private Mono<Tuple2<User, String[]>> extractCredentials(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return Mono.error(new IllegalStateException("Invalid or missing Authorization header"));
        }

        String base64Credentials = authHeader.substring(6);
        byte[] decodedCredentials = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedCredentials);
        String[] parts = credentials.split(":", 2);
        if (parts.length != 2) {
            return Mono.error(new IllegalStateException("Invalid format of credentials"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();
        return Mono.just(Tuples.of(authenticatedUser, parts));
    }

    private Mono<ResponseEntity<?>> validateAndPublish(QueueMappingRequest queueMappingRequest,
            Tuple2<User, String[]> userData) {
        String jsonMessage;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonMessage = objectMapper.writeValueAsString(queueMappingRequest.getMessage());
            if (jsonMessage == null)
                throw new IllegalStateException("Error converting message to JSON");
        } catch (JsonProcessingException e) {
            return Mono.error(new IllegalStateException("Error converting message to JSON", e));
        }

        User authenticatedUser = userData.getT1();
        String[] credentials = userData.getT2();
        Long publisherId = authenticatedUser.getId();
        String eventType = queueMappingRequest.getEvent();

        return webClient.post()
                .uri("/valid")
                .bodyValue(jsonMessage)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError(), clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                .toBodilessEntity()
                .then(findQueues(publisherId, eventType))
                .flatMap(queueMappings -> publishMessageToQueues(jsonMessage, authenticatedUser, credentials,
                        queueMappings))
                .then(Mono.just(ResponseEntity.ok("Message published successfully to all destination queues")));
    }

    private Mono<List<QueueMapping>> findQueues(Long publisherId, String eventType) {
        return Mono.defer(
                () -> Mono.just(queueMappingRepository.findByIdPublisherIdAndIdEventType(publisherId, eventType)))
                .flatMap(queueMappings -> queueMappings.isEmpty()
                        ? Mono.error(new IllegalStateException("No destination queues found"))
                        : Mono.just(queueMappings));
    }

    private Mono<Void> publishMessageToQueues(String jsonMessage, User user, String[] credentials,
            List<QueueMapping> queueMappings) {
        return Mono.fromCallable(() -> {
            try (Connection connection = rabbitConnectionFactory.newConnection();
                    Channel channel = connection.createChannel()) {
                for (QueueMapping qm : queueMappings) {
                    String destinationQueue = qm.getConsumerQueuename();
                    channel.basicPublish("", destinationQueue, null, jsonMessage.getBytes());
                }
            }
            return null;
        }).then();
    }

}

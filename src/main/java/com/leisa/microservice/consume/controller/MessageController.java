package com.leisa.microservice.consume.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.leisa.microservice.consume.Service.UserService;
import com.leisa.microservice.consume.model.User;
import com.leisa.microservice.consume.repository.UserRepository;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import jakarta.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@RestController
@RequestMapping("/consume")
public class MessageController {
    private BCryptPasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    private Logger logger = Logger.getLogger(MessageController.class.getName());

    private Map<String, UserConnection> userConnectionsMap = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public MessageController(UserService userService, UserRepository userRepository) throws IOException {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userRepository = userRepository;
    }

    @GetMapping("/start")
    public ResponseEntity<?> startConsuming(HttpServletRequest request) {

        try {

            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Authorization header");
            }

            String base64Credentials = authHeader.substring(6);
            byte[] decodedCredentials = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedCredentials);
            String[] parts = credentials.split(":", 2);
            if (parts.length != 2) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid format of credentials");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal();

            String username = authenticatedUser.getUsername();
            String password = parts[1];

            JSONObject config;

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.json");
            if (inputStream == null) {
                throw new IOException("config.json file not found in resources");
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String configJSON = reader.lines().collect(Collectors.joining("\n"));
                config = new JSONObject(configJSON);
            }

            ConnectionFactory userConnectionFactory = new ConnectionFactory();
            userConnectionFactory.setHost(config.getString("brokerHost"));
            userConnectionFactory.setPort(config.getInt("brokerPort"));

            userConnectionFactory.setUsername(username);
            userConnectionFactory.setPassword(password);

            Connection connection = userConnectionFactory.newConnection();
            Channel channel = connection.createChannel();

            UserConnection userConnection = new UserConnection();
            userConnection.setConnection(connection);
            userConnection.setChannel(channel);
            userConnectionsMap.put(username, userConnection);

            String queueName = authenticatedUser.getQueuename();
            logger.info(config.getString("brokerHost"));
            logger.info(config.getInt("brokerPort") + "");
            logger.info(username);
            logger.info(password);
            logger.info("Waiting for messages from queue: " + queueName);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

                messagingTemplate.convertAndSend("/topic/" + queueName, message);

                logger.info("Received message from '" + "/topic/" + queueName + "-->" + message + "'");

            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });

            return ResponseEntity.status(HttpStatus.OK).body("Started consuming messages from queue: " + queueName);
        } catch (Exception e) {
            logger.severe("Failed to start consuming messages: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to start consuming messages");
        }

    }

    @GetMapping("/stop")
    public ResponseEntity<?> stopConsuming(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Authorization header");
            }

            String base64Credentials = authHeader.substring(6);
            byte[] decodedCredentials = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedCredentials);
            String[] parts = credentials.split(":", 2);
            if (parts.length != 2) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid format of credentials");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal();

            String username = authenticatedUser.getUsername();
            //String password = parts[1];

            UserConnection userConnection = userConnectionsMap.get(username);
            if (userConnection != null) {
                Channel channel = userConnection.getChannel();
                Connection connection = userConnection.getConnection();

                if (channel != null && channel.isOpen()) {
                    channel.close();
                }
                if (connection != null && connection.isOpen()) {
                    connection.close();
                }

                userConnectionsMap.remove(username);

                logger.info("Stopped consuming messages for " + username);
                return ResponseEntity.ok("Stopped consuming messages for " + username);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No active session for " + username);
            }
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error stopping consumption for ");
        }
    }

    class UserConnection {
        private Connection connection;
        private Channel channel;

        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }

        public Connection getConnection() {
            return connection;
        }

        public Channel getChannel() {
            return channel;
        }
    }

}

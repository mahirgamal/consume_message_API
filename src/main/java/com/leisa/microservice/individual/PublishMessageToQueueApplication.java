package com.leisa.microservice.individual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.leisa.microservice.individual.model")
public class PublishMessageToQueueApplication {
    public static void main(String[] args) {
        SpringApplication.run(PublishMessageToQueueApplication.class, args);
    }
}
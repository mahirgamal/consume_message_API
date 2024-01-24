package com.leisa.microservice.consume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.leisa.microservice.consume.model")
public class ConsumeMessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumeMessageApplication.class, args);
    }
}
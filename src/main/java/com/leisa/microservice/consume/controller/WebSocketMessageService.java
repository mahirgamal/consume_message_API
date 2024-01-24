package com.leisa.microservice.consume.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketMessageService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessage(String message) {
        messagingTemplate.convertAndSend("/topic/public", message);
    }
}


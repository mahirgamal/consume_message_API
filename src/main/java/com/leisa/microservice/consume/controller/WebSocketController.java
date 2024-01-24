package com.leisa.microservice.consume.controller;




import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

	@MessageMapping("/message")
	@SendTo("/topic/public")
	public String send(String message) throws Exception {
		return message;
	}

}
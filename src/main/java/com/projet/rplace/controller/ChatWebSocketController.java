package com.projet.rplace.controller;

import com.projet.rplace.model.ChatMessage;
import com.projet.rplace.model.MessageRequest;
import com.projet.rplace.repository.ChatRepository;
import com.projet.rplace.security.JwtException;
import com.projet.rplace.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private final ChatRepository chatRepository;
    private final JwtProvider jwtProvider;

    @Autowired
    public ChatWebSocketController(ChatRepository chatRepository, JwtProvider jwtProvider) {
        this.chatRepository = chatRepository;
        this.jwtProvider = jwtProvider;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public ChatMessage sendChatMessage(MessageRequest messageRequest) {
        if (!jwtProvider.validateToken(messageRequest.getToken())) {
            throw new JwtException("Invalid token");
        }

        ChatMessage message = new ChatMessage();
        message.setUsername(jwtProvider.getUsername(messageRequest.getToken()));
        message.setContent(messageRequest.getContent());
        message.setTimestamp(System.currentTimeMillis());

        // Save the chat message to the database
        chatRepository.save(message);

        return message;
    }
}


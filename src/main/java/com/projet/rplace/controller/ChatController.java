package com.projet.rplace.controller;

import com.projet.rplace.model.ChatMessage;
import com.projet.rplace.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatRepository chatRepository;

    @Autowired
    public ChatController(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @GetMapping("/chat-history")
    public List<ChatMessage> getChatHistory() {
        return chatRepository.findTop100ByOrderByTimestampAsc();
    }
}

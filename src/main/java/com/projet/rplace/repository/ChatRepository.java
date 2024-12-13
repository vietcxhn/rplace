package com.projet.rplace.repository;

import com.projet.rplace.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    // Find the most recent 100 messages
    List<ChatMessage> findTop100ByOrderByTimestampAsc();
}

package com.projet.rplace.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CooldownServiceTest {

    @InjectMocks
    private CooldownService cooldownService;

    private final Map<String, Long> cooldowns = new HashMap<>();

    @Test
    void testSetCooldown() {
        cooldownService.setCooldown("testuser", Instant.now());
        assertTrue(cooldowns.containsKey("testuser"));
    }

    @Test
    void testCanPlacePixel() {
        cooldowns.put("testuser", System.currentTimeMillis() - 60000L);
        assertTrue(cooldownService.canPlacePixel("testuser"));
    }
}

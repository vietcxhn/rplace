package com.projet.rplace.services;

import com.projet.rplace.model.XUser;
import com.projet.rplace.repository.XUserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CooldownService {
    XUserRepository xUserRepository;
    private final Map<String, Instant> userCooldowns = new ConcurrentHashMap<>();
    private static final int COOLDOWN_SECONDS = 300; // 5 minutes

    public CooldownService(XUserRepository xUserRepository) {
        this.xUserRepository = xUserRepository;
    }

    public boolean canPlacePixel(String username) {
        Optional<XUser> oUser = xUserRepository.findByUsername(username);
        if (oUser.isEmpty()) {
            return false;
        }
        XUser user = oUser.get();
        if (user.getRoles().contains("ADMIN")) {
            return true;
        }

        Instant now = Instant.now();
        return userCooldowns.getOrDefault(username, Instant.MIN).isBefore(now);
    }

    public long getCooldown(String username) {
        Instant now = Instant.now();
        Duration duration = Duration.between(now, userCooldowns.getOrDefault(username, now));
        return duration.toMillis();
    }

    public void setCooldown(String username, Instant now) {
        userCooldowns.put(username, now.plusSeconds(COOLDOWN_SECONDS));
    }
}


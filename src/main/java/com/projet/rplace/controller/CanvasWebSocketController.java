package com.projet.rplace.controller;

import com.projet.rplace.model.Pixel;
import com.projet.rplace.model.PlacePixelRequest;
import com.projet.rplace.security.JwtException;
import com.projet.rplace.security.JwtProvider;
import com.projet.rplace.services.CooldownService;
import com.projet.rplace.services.PixelService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class CanvasWebSocketController {
    private final PixelService pixelService;
    private final CooldownService cooldownService;
    private final JwtProvider jwtProvider;

    public CanvasWebSocketController(PixelService pixelService, CooldownService cooldownService, JwtProvider jwtProvider) {
        this.pixelService = pixelService;
        this.cooldownService = cooldownService;
        this.jwtProvider = jwtProvider;
    }

    @MessageMapping("/update")
    @SendTo("/topic/canvas")
    public Pixel updatePixel(PlacePixelRequest request) throws Exception {
        // check token
        if (!jwtProvider.validateToken(request.getToken())) {
            throw new JwtException("Invalid token");
        }

        Pixel pixel = request.getPixel();
        String username = jwtProvider.getUsername(request.getToken());

        // Check cooldown
        if (!cooldownService.canPlacePixel(username)) {
            throw new IllegalAccessException("Cooldown in effect");
        }

        // Update pixel
        Pixel updatedPixel = pixelService.updatePixel(pixel.getX(), pixel.getY(), pixel.getColor());

        // Set cooldown
        cooldownService.setCooldown(username, Instant.now());

        // Broadcast updated pixel
        return updatedPixel;
    }
}

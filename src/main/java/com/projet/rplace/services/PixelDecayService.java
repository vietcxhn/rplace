package com.projet.rplace.services;

import com.projet.rplace.model.Color;
import com.projet.rplace.model.Pixel;
import com.projet.rplace.repository.PixelRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PixelDecayService {
    private final SimpMessagingTemplate messagingTemplate;
    private final PixelRepository pixelRepository;

    private static final long DECAY_THRESHOLD = 1000 * 60 * 60 * 24; // 1 day
    private static final Color DEFAULT_COLOR = Color.COLOR_FFFFFF; // Decay color (white)

    public PixelDecayService(SimpMessagingTemplate messagingTemplate, PixelRepository pixelRepository) {
        this.messagingTemplate = messagingTemplate;
        this.pixelRepository = pixelRepository;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60) // Run every hour
    @Transactional
    public void decayPixels() {
        System.out.println("Decaying pixels");
        long currentTime = System.currentTimeMillis();

        List<Pixel> pixelsToDecay = pixelRepository.findAll().stream()
            .filter(pixel -> currentTime - pixel.getLastUpdated() > DECAY_THRESHOLD)
        .toList();

        // Decay each pixel by setting it to the default color
        for (Pixel pixel : pixelsToDecay) {
            pixel.setColor(DEFAULT_COLOR);
            pixel.setLastUpdated(currentTime); // Reset the timestamp to avoid multiple updates
            pixelRepository.save(pixel);
        }

        if (!pixelsToDecay.isEmpty()) {
            pixelsToDecay.forEach(this::broadcastPixelUpdate);
        }
    }

    private void broadcastPixelUpdate(Pixel pixel) {
        messagingTemplate.convertAndSend("/topic/canvas", pixel);
    }
}

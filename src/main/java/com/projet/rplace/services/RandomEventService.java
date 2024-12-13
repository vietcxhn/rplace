package com.projet.rplace.services;

import com.projet.rplace.model.Color;
import com.projet.rplace.model.Pixel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Random;

@Service
public class RandomEventService {

    private final SimpMessagingTemplate messagingTemplate;
    private final PixelService pixelService;
    private final Random random = new Random();

    public RandomEventService(SimpMessagingTemplate messagingTemplate, PixelService pixelService) {
        this.messagingTemplate = messagingTemplate;
        this.pixelService = pixelService;
    }

    // Schedule random events 5 seconds
    @Scheduled(fixedRate = 5000)
    public void triggerRandomEvent() {
        System.out.println("Random event triggered");

        String[] eventTypes = {"RANDOM_PIXEL_CHANGE", "RANDOM_AREA_CHANGE"};
        String eventType = eventTypes[random.nextInt(eventTypes.length)];

        // Change color of random pixel
        if (eventType.equals("RANDOM_PIXEL_CHANGE")) {
            int x = random.nextInt(1000);
            int y = random.nextInt(1000);
            Color color = Color.randomColor(); // Random color

            Pixel pixel = pixelService.updatePixel(x, y, color);

            messagingTemplate.convertAndSend("/topic/canvas", pixel);
        }
        // Change color of random area of pixels
        else if (eventType.equals("RANDOM_AREA_CHANGE")) {

        }
    }
}

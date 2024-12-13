package com.projet.rplace.controller;

import com.projet.rplace.model.Color;
import com.projet.rplace.model.Pixel;
import com.projet.rplace.services.CooldownService;
import com.projet.rplace.services.PixelService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CanvasControllerTest {

    @MockBean
    PixelService pixelService;

    @MockBean
    CooldownService cooldownService;

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate = new RestTemplate();

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/canvas";
    }

    @Test
    void testGetCanvas() {
        when(pixelService.getCanvas()).thenReturn(List.of(
                new Pixel(1, 1, Color.COLOR_FFFFFF),
                new Pixel(2, 2, Color.COLOR_FFFFFF)
        ));

        ResponseEntity<Pixel[]> response = restTemplate.getForEntity(baseUrl, Pixel[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Pixel[] pixels = response.getBody();
        assertNotNull(pixels);
        assertTrue(pixels.length == 2);
    }

    @Test
    void testGetCooldown() {
        String username = "user";
        when(cooldownService.getCooldown(username)).thenReturn(100L);
        String url = baseUrl + "/cooldown?username=" + username;

        ResponseEntity<Long> response = restTemplate.exchange(
                url, HttpMethod.GET, null, Long.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(100L, (long) response.getBody());
    }

    @Test
    void testGetColorPalette() {
        String url = baseUrl + "/color-palette";
        ResponseEntity<Color[]> response = restTemplate.exchange(
                url, HttpMethod.GET, null, Color[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(Objects.requireNonNull(response.getBody()).length, Color.values().length);
    }
}

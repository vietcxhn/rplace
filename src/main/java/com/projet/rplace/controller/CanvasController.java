package com.projet.rplace.controller;

import com.projet.rplace.model.Color;
import com.projet.rplace.model.Pixel;
import com.projet.rplace.services.CooldownService;
import com.projet.rplace.services.PixelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/canvas")
public class CanvasController {
    private final PixelService pixelService;
    private final CooldownService cooldownService;

    @Autowired
    public CanvasController(PixelService pixelService, CooldownService cooldownService) {
        this.pixelService = pixelService;
        this.cooldownService = cooldownService;
    }

    @GetMapping
    public List<Pixel> getCanvas() {
        return pixelService.getCanvas();
    }

    @GetMapping("/cooldown")
    public long getCooldown(@RequestParam String username) {
        return cooldownService.getCooldown(username);
    }

    @GetMapping("/color-palette")
    public Color[] getColorPalette() {
        return Color.values();
    }
}

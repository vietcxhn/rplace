package com.projet.rplace.services;

import com.projet.rplace.model.Color;
import com.projet.rplace.model.Pixel;
import com.projet.rplace.repository.PixelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class PixelService {
    private final PixelRepository pixelRepository;

    @Autowired
    public PixelService(PixelRepository pixelRepository) {
        this.pixelRepository = pixelRepository;
    }

    public List<Pixel> getCanvas() {
        return pixelRepository.findAll();
    }

    public Pixel updatePixel(int x, int y, Color color) {
        Pixel pixel = pixelRepository.findByXAndY(x, y);
        if (pixel == null) {
            pixel = new Pixel();
            pixel.setX(x);
            pixel.setY(y);
        }
        pixel.setColor(color);
        pixel.setLastUpdated(System.currentTimeMillis());
        return pixelRepository.save(pixel);
    }
}

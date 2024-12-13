package com.projet.rplace.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Pixel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int x;
    private int y;
    private Color color;
    private long lastUpdated;

    public Pixel(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.lastUpdated = System.currentTimeMillis();
    }

    public Pixel() {

    }
}

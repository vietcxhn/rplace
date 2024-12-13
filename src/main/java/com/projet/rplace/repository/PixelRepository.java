package com.projet.rplace.repository;

import com.projet.rplace.model.Pixel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PixelRepository extends JpaRepository<Pixel, Long> {
    List<Pixel> findAll();
    Pixel findByXAndY(int x, int y);
}

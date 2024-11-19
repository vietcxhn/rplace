package com.projet.gestioncv.repository;

import com.projet.gestioncv.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByCvId(long cvId);
    List<Activity> findByTitleContaining(String title);
}

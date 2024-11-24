package com.projet.gestioncv.repository;

import com.projet.gestioncv.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByPersonUsername(String username);
    Page<Activity> findByPersonUsernameOrderByActivityYearAsc(String username, Pageable pageable);
    Optional<Activity> findByTitleContaining(String title);
}

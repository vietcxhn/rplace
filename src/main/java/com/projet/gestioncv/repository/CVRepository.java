package com.projet.gestioncv.repository;

import com.projet.gestioncv.model.CV;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CVRepository extends JpaRepository<CV, Long> {
}

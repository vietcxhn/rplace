package com.projet.rplace.repository;

import com.projet.rplace.model.XUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface XUserRepository extends JpaRepository<XUser, Long> {
    Optional<XUser> findByUsername(String username);
    boolean existsByUsername(String username);
}

package com.projet.gestioncv.repository;

import com.projet.gestioncv.model.Person;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, String> {
    @Query("SELECT DISTINCT p FROM Person p " +
            "LEFT JOIN p.cv.activities a " +
            "WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Person> search(@Param("query") String query, Pageable pageable);
}

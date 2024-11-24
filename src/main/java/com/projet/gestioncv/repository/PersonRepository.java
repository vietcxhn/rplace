package com.projet.gestioncv.repository;

import com.projet.gestioncv.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, String> {
    @Query("SELECT DISTINCT p FROM Person p " +
            "WHERE (LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR EXISTS (SELECT a FROM Activity a WHERE a.person = p AND LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%'))))" +
            "AND 'ADMIN' NOT MEMBER OF p.roles " +
            "ORDER BY p.username ASC")
    Page<Person> search(@Param("query") String query, Pageable pageable);
}

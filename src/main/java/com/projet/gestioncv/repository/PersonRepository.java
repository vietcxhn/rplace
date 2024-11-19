package com.projet.gestioncv.repository;

import com.projet.gestioncv.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, String> {
    List<Person> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);
}

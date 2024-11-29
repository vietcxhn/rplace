package com.projet.gestioncv.service;

import com.projet.gestioncv.model.Person;
import com.projet.gestioncv.repository.PersonRepository;
import com.projet.gestioncv.security.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public Person createPerson(Person person) {
        if (personRepository.existsById(person.getUsername())) {
            throw new JwtException("Username is already in use");
        }

        return personRepository.save(person);
    }

    public Optional<Person> getPerson(String id) {
        return personRepository.findById(id);
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Person updatePerson(Person updatedPerson) {
        return personRepository.save(updatedPerson);
    }

    public void deletePerson(String username) {
        personRepository.deleteById(username);
    }

    public Page<Person> searchPersons(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return personRepository.search(query, pageable);
    }
}


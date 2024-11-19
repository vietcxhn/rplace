package com.projet.gestioncv.service;

import com.projet.gestioncv.model.Person;
import com.projet.gestioncv.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    public Optional<Person> getPerson(String id) {
        return personRepository.findById(id);
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Person updatePerson(String username, Person updatedPerson) {
        updatedPerson.setUsername(username);
        return personRepository.save(updatedPerson);
    }

    public void deletePerson(String username) {
        personRepository.deleteById(username);
    }

    public List<Person> searchPersons(String keyword) {
        return personRepository.findByFirstNameContainingOrLastNameContaining(keyword, keyword);
    }
}


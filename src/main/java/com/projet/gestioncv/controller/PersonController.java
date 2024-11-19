package com.projet.gestioncv.controller;

import com.projet.gestioncv.model.Person;
import com.projet.gestioncv.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        Person created = personService.createPerson(person);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Person> getPerson(@PathVariable String username) {
        Optional<Person> person = personService.getPerson(username);
        return person.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Person> getAllPersons() {
        return personService.getAllPersons();
    }

    @PutMapping("/{username}")
    public ResponseEntity<Person> updatePerson(@PathVariable String username, @RequestBody Person updatedPerson) {
        Person updated = personService.updatePerson(username, updatedPerson);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deletePerson(@PathVariable String username) {
        personService.deletePerson(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Person> searchPersons(@RequestParam String keyword) {
        return personService.searchPersons(keyword);
    }
}


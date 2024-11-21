package com.projet.gestioncv.controller;

import com.projet.gestioncv.dto.PersonDTO;
import com.projet.gestioncv.model.Person;
import com.projet.gestioncv.service.PersonService;
import com.projet.gestioncv.service.PopulateService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/persons")
public class PersonController {
    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private PersonService personService;

    @Autowired
    private PopulateService populateService;

    @PostMapping
    public ResponseEntity<PersonDTO> createPerson(@RequestBody PersonDTO personDTO) {
        Person person = map(personDTO);
        personService.createPerson(person);
        return ResponseEntity.ok(personDTO);
    }

    @GetMapping("/{username}")
    public ResponseEntity<PersonDTO> getPerson(@PathVariable String username) {
        Optional<Person> person = personService.getPerson(username);
        return person.map(this::map).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<PersonDTO> getAllPersons() {
        return personService.getAllPersons().stream().map(this::map).toList();
    }

    @PutMapping("/{username}")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable String username, @RequestBody PersonDTO updatedPerson) {
        Person updated = personService.updatePerson(username, map(updatedPerson));
        return ResponseEntity.ok(updatedPerson);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deletePerson(@PathVariable String username) {
        personService.deletePerson(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<PersonDTO> searchPersons(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return personService.searchPersons(query, page, size).stream().map(this::map).toList();
    }

    @GetMapping("/populate")
    public ResponseEntity<Void> populate(
            @RequestParam(defaultValue = "0") int nbPerson,
            @RequestParam(defaultValue = "10") int nbActivity) {
        populateService.populate(nbPerson, nbActivity);
        return ResponseEntity.noContent().build();
    }


    private Person map(PersonDTO dto) {
        return modelMapper.map(dto,  Person.class);
    }

    private PersonDTO map(Person dto) {
        return modelMapper.map(dto,  PersonDTO.class);
    }
}


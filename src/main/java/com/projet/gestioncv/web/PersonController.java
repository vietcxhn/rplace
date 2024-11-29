package com.projet.gestioncv.web;

import com.projet.gestioncv.dto.PersonDTO;
import com.projet.gestioncv.model.Person;
import com.projet.gestioncv.security.JwtException;
import com.projet.gestioncv.service.PersonService;
import com.projet.gestioncv.service.PopulateService;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/persons")
public class PersonController {
    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private PersonService personService;

    @Autowired
    private PopulateService populateService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Map<String, String>> createPerson(@RequestBody Person person) {
        person.setActivities(new ArrayList<>());
        person.setRoles(Set.of("USER"));
        Map<String, String> errors = checkError(person);
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        try {
            if (errors.isEmpty())
                personService.createPerson(person);
        } catch (JwtException e) {
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok(errors);
    }

    @GetMapping("/{username}")
    public ResponseEntity<PersonDTO> getPerson(@PathVariable String username) {
        Optional<Person> person = personService.getPerson(username);
        return person.map(this::map).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{username}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('USER') and principal.username == #username)")
    public ResponseEntity<Map<String, String>> updatePerson(@PathVariable String username, @RequestBody PersonDTO updatedPerson) {
        Person update = map(updatedPerson);
        Person old = personService.getPerson(username).get();
        update.setUsername(username);
        update.setActivities(old.getActivities());
        update.setRoles(old.getRoles());
        update.setPassword(old.getPassword());
        Map<String, String> errorMap = checkError(update);
        if(errorMap.isEmpty())
            personService.updatePerson(update);
        return ResponseEntity.ok(errorMap);
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deletePerson(@PathVariable String username) {
        personService.deletePerson(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<PersonDTO>> searchPersons(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PersonDTO> personDTOPage = personService.searchPersons(query, page, size).map(this::map);
        return ResponseEntity.ok(personDTOPage);
    }

    @GetMapping("/populate")
    @PreAuthorize("hasAuthority('ADMIN')")
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

    @Autowired
    LocalValidatorFactoryBean validationFactory;

    private Map<String, String> checkError(Person p) {
        Set<ConstraintViolation<Person>> errors = validationFactory.validate(p);
        Map<String, String> errorMap = new HashMap<>();
        if (!errors.isEmpty()) {
            for (ConstraintViolation<Person> violation : errors) {
                errorMap.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
        }
        return errorMap;
    }
}


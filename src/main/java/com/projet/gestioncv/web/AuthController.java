package com.projet.gestioncv.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.projet.gestioncv.dto.PersonDTO;
import com.projet.gestioncv.model.Person;
import com.projet.gestioncv.security.JwtException;
import com.projet.gestioncv.service.JwtUserService;
import com.projet.gestioncv.service.PersonService;
import com.projet.gestioncv.service.PopulateService;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.*;


/**
 * L'API d'authentification
 */
@RestController
@RequestMapping("/auth")
@Profile("usejwt")
public class AuthController {

    @Autowired
    private JwtUserService userService;

    @Autowired
    private PersonService personService;

    @Autowired
    LocalValidatorFactoryBean validationFactory;

    @Autowired
    PasswordEncoder passwordEncoder;

    private ModelMapper modelMapper = new ModelMapper();

    @PostMapping("/login")
    public String login(//
                        @RequestParam String username, //
                        @RequestParam String password) {
        return userService.login(username, password);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String bearerToken) {
        String jwt = bearerToken.substring(7);
        userService.removeToken(jwt);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<PersonDTO> me(Principal user) {
        if (user == null || user.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        var personDTO = personService.getPerson(user.getName()).map(this::map);
        return ResponseEntity.of(personDTO);
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public String refresh(Principal user) {
        return userService.refresh(user.getName());
    }

    private Person map(PersonDTO dto) {
        return modelMapper.map(dto,  Person.class);
    }

    private PersonDTO map(Person dto) {
        return modelMapper.map(dto,  PersonDTO.class);
    }

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
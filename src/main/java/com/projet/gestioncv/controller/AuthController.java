package com.projet.gestioncv.controller;

import java.security.Principal;

import com.projet.gestioncv.dto.PersonDTO;
import com.projet.gestioncv.model.Person;
import com.projet.gestioncv.service.JwtUserService;
import com.projet.gestioncv.service.PopulateService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * L'API d'authentification
 */
@RestController
@RequestMapping("/auth")
@Profile("usejwt")
public class AuthController {

    @Autowired
    private JwtUserService userService;

    private ModelMapper modelMapper = new ModelMapper();

    /**
     * Authentification et récupération d'un JWT
     */
    @PostMapping("/login")
    public String login(//
                        @RequestParam String username, //
                        @RequestParam String password) {
        return userService.login(username, password);
    }

    /**
     * Ajouter un utilisateur
     */
    @PostMapping("/signup")
    public String signup(@RequestBody PersonDTO user) {
        var xuser = map(user);
        xuser.setPassword(xuser.getUsername() + "1234");
        return userService.signup(xuser);
    }

    /**
     * Supprimer un utilisateur
     */
    @DeleteMapping("/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String delete(@PathVariable String username) {
        System.out.println("delete " + username);
        userService.delete(username);
        return username;
    }

    /**
     * Récupérer des informations sur un utilisateur
     */
    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PersonDTO> search(@PathVariable String userName) {
        var userDto = userService.search(userName).map(this::map);
        return ResponseEntity.of(userDto);
    }

    /**
     * Récupérer des informations sur l'utilisateur courant
     */
    @GetMapping(value = "/me")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<PersonDTO> whoami(Principal user) {
        return search(user.getName());
    }

    /**
     * Récupérer un nouveau JWT
     */
    @GetMapping("/refresh")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public String refresh(Principal user) {
        return userService.refresh(user.getName());
    }

    /**
     * Fonctions utilitaires
     */
    private Person map(PersonDTO dto) {
        return modelMapper.map(dto,  Person.class);
    }

    private PersonDTO map(Person dto) {
        return modelMapper.map(dto,  PersonDTO.class);
    }


    @Autowired
    private PopulateService populateService;

    @GetMapping("/populate")
    public ResponseEntity<Void> populate(
            @RequestParam(defaultValue = "0") int nbPerson,
            @RequestParam(defaultValue = "10") int nbActivity) {
        populateService.populate(nbPerson, nbActivity);
        return ResponseEntity.noContent().build();
    }
}
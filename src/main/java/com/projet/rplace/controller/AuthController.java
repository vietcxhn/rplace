package com.projet.rplace.controller;


import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.projet.rplace.model.XUser;
import com.projet.rplace.model.XUserDTO;
import com.projet.rplace.security.JwtException;
import com.projet.rplace.services.JwtUserService;
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
public class AuthController {

    @Autowired
    private JwtUserService userService;

    @Autowired
    LocalValidatorFactoryBean validationFactory;

    @Autowired
    PasswordEncoder passwordEncoder;

    private ModelMapper modelMapper = new ModelMapper();

    @PostMapping("/login")
    public ResponseEntity<String> login(//
                        @RequestParam String username, //
                        @RequestParam String password) {
        try {
            return ResponseEntity.ok(userService.login(username, password));
        } catch (JwtException e) {
            System.out.println(e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody XUser user) {
        user.setRoles(Set.of("USER"));
        Map<String, String> errors = checkError(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            if (errors.isEmpty()) {
                String token = userService.signup(user);
                errors.put("token", token);
            }
        } catch (JwtException e) {
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok(errors);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String bearerToken) {
        String jwt = bearerToken.substring(7);
        userService.removeToken(jwt);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<XUserDTO> me(Principal user) {
        if (user == null || user.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        var personDTO = userService.getUser(user.getName()).map(this::map);
        return ResponseEntity.of(personDTO);
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public String refresh(Principal user) {
        return userService.refresh(user.getName());
    }

    private XUser map(XUserDTO dto) {
        return modelMapper.map(dto,  XUser.class);
    }

    private XUserDTO map(XUser dto) {
        return modelMapper.map(dto,  XUserDTO.class);
    }

    private Map<String, String> checkError(XUser p) {
        Set<ConstraintViolation<XUser>> errors = validationFactory.validate(p);
        Map<String, String> errorMap = new HashMap<>();
        if (!errors.isEmpty()) {
            for (ConstraintViolation<XUser> violation : errors) {
                errorMap.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
        }
        return errorMap;
    }
}
package com.projet.gestioncv.dto;

import com.projet.gestioncv.model.Activity;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class PersonDTO {
    private String username;
    private String lastName;
    private String firstName;
    private String email;
    private String website;
    private Date birthDate;
    private Set<String> roles;
}

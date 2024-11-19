package com.projet.gestioncv.dto;

import com.projet.gestioncv.model.CV;
import lombok.Data;

import java.util.Date;

@Data
public class PersonDTO {
    private String lastName;
    private String firstName;
    private String email;
    private String website;
    private Date birthDate;
    private CV cv;
}

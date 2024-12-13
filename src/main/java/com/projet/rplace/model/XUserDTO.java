package com.projet.rplace.model;

import lombok.Data;

import java.util.Set;

@Data
public class XUserDTO {
    private Long id;
    private String username;
    Set<String> roles;
}

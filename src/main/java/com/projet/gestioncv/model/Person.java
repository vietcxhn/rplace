package com.projet.gestioncv.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Person {
    @EqualsAndHashCode.Include
    @Id
    private String username;

    @Basic
    private String lastName;

    @Basic
    private String firstName;

    @Basic
    private String email;

    @Basic
    private String website;

    @Basic
    private Date birthDate;

    @Basic
    private String password;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cv_id", referencedColumnName = "id")
    private CV cv;

    @ElementCollection(fetch = FetchType.EAGER)
    Set<String> roles;

    public Person() {

    }
}

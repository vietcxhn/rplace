package com.projet.gestioncv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Basic
    @NotNull
    private int activityYear;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ActivityNature nature;

    @Basic
    @NotBlank
    private String title;

    @Basic
    private String description;

    @Basic
    private String website;

    @ManyToOne
    private CV cv;

    public Activity() {

    }
}

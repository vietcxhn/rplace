package com.projet.gestioncv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Activity {
    @EqualsAndHashCode.Include
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
    @JsonIgnore
    private CV cv;

    public Activity() {

    }
}

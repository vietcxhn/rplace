package com.projet.gestioncv.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
    @NotNull(message = "L'année de l'activité est obligatoire")
    @Min(value = 1900, message = "L'année de l'activité ne peut pas être inférieure à 1900")
    @Max(value = 2024, message = "L'année de l'activité ne peut pas être supérieure à 2024")
    private int activityYear;

    @NotNull(message = "La nature de l'activité est obligatoire")
    @Enumerated(EnumType.STRING)
    private ActivityNature nature;

    @Basic
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 100, message = "Le titre ne peut pas dépasser 100 caractères")
    private String title;

    @Basic
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    @Basic
    @Pattern(
            regexp = "^(http://|https://)?(www\\.)?[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/.*)?$|^$",
            message = "Le site web doit être une URL valide"
    )
    private String website;

    @ManyToOne
    @ToString.Exclude
    @JsonBackReference
    private Person person;

    public Activity() {

    }
}

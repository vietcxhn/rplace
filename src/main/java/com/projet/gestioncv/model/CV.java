package com.projet.gestioncv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class CV {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JsonIgnore
    private Person person;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Activity> activities = new ArrayList<>();

    public void addActivity(Activity activity) {
        this.activities.add(activity);
        activity.setCv(this);
    }
}

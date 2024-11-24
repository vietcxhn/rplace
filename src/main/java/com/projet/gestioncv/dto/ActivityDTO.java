package com.projet.gestioncv.dto;

import com.projet.gestioncv.model.ActivityNature;
import lombok.Data;

@Data
public class ActivityDTO {
    private long id;
    private int activityYear;
    private ActivityNature nature;
    private String title;
    private String description;
    private String website;
}

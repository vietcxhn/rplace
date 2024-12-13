package com.projet.rplace.model;

import lombok.Data;

import java.time.Instant;

@Data
public class UserCooldown {
    private String userId;
    private Instant cooldownExpiry;

}
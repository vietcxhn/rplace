package com.projet.gestioncv.service;

import com.github.javafaker.Faker;
import com.projet.gestioncv.model.Activity;
import com.projet.gestioncv.model.ActivityNature;
import com.projet.gestioncv.model.CV;
import com.projet.gestioncv.model.Person;
import com.projet.gestioncv.repository.ActivityRepository;
import com.projet.gestioncv.repository.CVRepository;
import com.projet.gestioncv.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class PopulateService {
    @Autowired
    private PersonRepository personRepository;;
    @Autowired
    private CVRepository cvRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    public void populate(int nbPersons, int maxActivitiesPerCv) {
        for (int i = 0; i < nbPersons; i++) {
            System.out.println("create " + i);
            Person person = new Person();
            person.setUsername(faker.name().username());
            person.setFirstName(faker.name().firstName());
            person.setLastName(faker.name().lastName());
            person.setEmail(faker.internet().emailAddress());
            person.setWebsite(faker.internet().url());
            person.setBirthDate(faker.date().birthday());
            person.setPassword(passwordEncoder.encode(faker.internet().password()));
            person.setRoles(Set.of("ADMIN", "USER"));

            CV cv = new CV();
            person.setCv(cv);
            cv.setPerson(person);
            person = personRepository.save(person);
            cv = person.getCv();
            int nbActivities = random.nextInt(maxActivitiesPerCv) + 1;
            for (int j = 0; j < nbActivities; j++) {
                Activity activity = new Activity();
                activity.setActivityYear(2000 + random.nextInt(24));
                activity.setNature(ActivityNature.values()[random.nextInt(ActivityNature.values().length)]);
                activity.setTitle(faker.company().profession());
                activity.setDescription(faker.lorem().sentence());
                activity.setWebsite(random.nextBoolean() ? faker.internet().url() : null);

                // Ajouter l'activité à la liste du CV
                cv.addActivity(activity);
            }
            cvRepository.save(cv);
        }
    }
}

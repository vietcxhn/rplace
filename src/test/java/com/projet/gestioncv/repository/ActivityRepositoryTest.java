package com.projet.gestioncv.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.projet.gestioncv.model.Activity;
import com.projet.gestioncv.model.ActivityNature;
import com.projet.gestioncv.model.CV;
import com.projet.gestioncv.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class ActivityRepositoryTest {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testCreateAndFindActivity() {
        Person person = new Person();
        person.setUsername("username1");
        person.setLastName("lastname1");
        person.setFirstName("firstname1");
        person.setEmail("person1@gmail.com");
        person.setPassword("password");

        CV cv = new CV();
        cv.setPerson(person);
        person.setCv(cv);
        personRepository.save(person);

        Activity activity = new Activity();
        activity.setActivityYear(2023);
        activity.setNature(ActivityNature.PROFESSIONAL_EXPERIENCE);
        activity.setTitle("Developer");
        activity.setCv(cv);
        activityRepository.save(activity);

        List<Activity> activities = activityRepository.findByCvId(cv.getId());
        assertFalse(activities.isEmpty());
        assertEquals("Developer", activities.getFirst().getTitle());
    }

    @Test
    public void testUpdateActivity() {
        Person person = new Person();
        person.setUsername("username2");
        person.setLastName("lastname2");
        person.setFirstName("firstname2");
        person.setEmail("person2@gmail.com");
        person.setPassword("password");

        CV cv = new CV();
        cv.setPerson(person);
        person.setCv(cv);
        personRepository.save(person);

        Activity activity = new Activity();
        activity.setActivityYear(2022);
        activity.setNature(ActivityNature.EDUCATION);
        activity.setTitle("formation");
        activity.setCv(cv);
        activityRepository.save(activity);

        activity.setTitle("formation spring boot");
        activityRepository.save(activity);

        Optional<Activity> updatedActivity = activityRepository.findById(activity.getId());
        assertTrue(updatedActivity.isPresent());
        assertEquals("formation spring boot", updatedActivity.get().getTitle());
    }

    @Test
    public void testDeleteActivity() {
        Person person = new Person();
        person.setUsername("username3");
        person.setLastName("lastname3");
        person.setFirstName("firstname3");
        person.setEmail("person3@gmail.com");
        person.setPassword("password");

        CV cv = new CV();
        cv.setPerson(person);
        person.setCv(cv);
        personRepository.save(person);

        Activity activity = new Activity();
        activity.setActivityYear(2021);
        activity.setNature(ActivityNature.PROJECT);
        activity.setTitle("project");
        activity.setCv(cv);
        activityRepository.save(activity);

        activityRepository.delete(activity);
        Optional<Activity> deletedActivity = activityRepository.findById(activity.getId());
        assertFalse(deletedActivity.isPresent());
    }
}

package com.projet.gestioncv.service;

import com.projet.gestioncv.model.Activity;
import com.projet.gestioncv.model.ActivityNature;
import com.projet.gestioncv.model.Person;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ActivityServiceTest {

    @Autowired
    private ActivityService activityService;

    @Test
    public void test() {
        Activity activity = new Activity();
        activity.setTitle("title");
        activity.setActivityYear(2024);
        activity.setNature(ActivityNature.EDUCATION);

        Activity created = activityService.createActivity(activity);
        assertEquals("title", created.getTitle());

        Optional<Activity> found = activityService.getActivity(created.getId());
        assertTrue(found.isPresent());
        assertEquals("title", found.get().getTitle());

        activity.setTitle("updated title");
        Activity result = activityService.updateActivity(created.getId(), activity);
        assertEquals("updated title", result.getTitle());

        activityService.deleteActivity(created.getId());
        found = activityService.getActivity(created.getId());
        assertTrue(found.isEmpty());
    }
}
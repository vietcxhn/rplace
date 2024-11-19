package com.projet.gestioncv.service;

import com.projet.gestioncv.model.Activity;
import com.projet.gestioncv.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public Optional<Activity> getActivity(Long id) {
        return activityRepository.findById(id);
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Activity updateActivity(Long id, Activity updatedActivity) {
        updatedActivity.setId(id);
        return activityRepository.save(updatedActivity);
    }

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    public List<Activity> searchActivitiesByTitle(String keyword) {
        return activityRepository.findByTitleContaining(keyword);
    }
}


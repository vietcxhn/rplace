package com.projet.gestioncv.controller;

import com.projet.gestioncv.model.Activity;
import com.projet.gestioncv.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {
        Activity created = activityService.createActivity(activity);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivity(@PathVariable Long id) {
        Optional<Activity> activity = activityService.getActivity(id);
        return activity.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Activity>> getActivitiesByCv(
            @RequestParam Long cvId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Activity> activities = activityService.getActivitiesByCvId(cvId, page, size);
        return ResponseEntity.ok(activities.getContent());
    }
    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @RequestBody Activity updatedActivity) {
        Activity updated = activityService.updateActivity(id, updatedActivity);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Activity> searchActivities(@RequestParam String keyword) {
        return activityService.searchActivitiesByTitle(keyword);
    }
}

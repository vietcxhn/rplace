package com.projet.gestioncv.web;

import com.projet.gestioncv.dto.ActivityDTO;
import com.projet.gestioncv.model.Activity;
import com.projet.gestioncv.service.ActivityService;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    private final ModelMapper modelMapper = new ModelMapper();
    private Activity map(ActivityDTO dto) {
        return modelMapper.map(dto,  Activity.class);
    }

    private ActivityDTO map(Activity dto) {
        return modelMapper.map(dto,  ActivityDTO.class);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('USER') and principal.username == #personId)")
    public ResponseEntity<Map<String, String>> createActivity(
            @RequestBody ActivityDTO activityDTO,
            @RequestParam String personId
    ) {
        Activity activity = map(activityDTO);
        Map<String, String> errorMap = checkError(activity);
        if (errorMap.isEmpty())
            activityService.addActivityToPerson(personId, activity);
        return ResponseEntity.ok(errorMap);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivity(@PathVariable Long id) {
        Optional<ActivityDTO> activity = activityService.getActivity(id).map(this::map);
        return activity.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ActivityDTO>> getActivitiesByUsername(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ActivityDTO> activities =
                activityService.getActivitiesByPersonUsername(username, page, size).map(this::map);
        return ResponseEntity.ok(activities);
    }

    @PutMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ADMIN') or " +
            "(hasAuthority('USER') and principal.username == @activityService.getActivity(#id).get().getPerson().getUsername())")
    public ResponseEntity<Map<String, String>> updateActivity(@PathVariable Long id, @RequestBody Activity updatedActivity) {
        Map<String, String> errorMap = checkError(updatedActivity);
        if (errorMap.isEmpty())
            activityService.updateActivity(id, updatedActivity);
        return ResponseEntity.ok(errorMap);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ADMIN') or " +
            "(hasAuthority('USER') and principal.username == @activityService.getActivity(#id).get().getPerson().getUsername())")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Optional<ActivityDTO> searchActivities(@RequestParam String keyword) {
        return activityService.searchActivitiesByTitle(keyword).map(this::map);
    }

    @Autowired
    LocalValidatorFactoryBean validationFactory;

    private Map<String, String> checkError(Activity a) {
        Set<ConstraintViolation<Activity>> errors = validationFactory.validate(a);
        Map<String, String> errorMap = new HashMap<>();
        if (!errors.isEmpty()) {
            for (ConstraintViolation<Activity> violation : errors) {
                errorMap.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
        }
        return errorMap;
    }
}

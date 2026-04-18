package com.devStephen.profiler_api.controller;

import com.devStephen.profiler_api.dto.CreateProfileRequest;
import com.devStephen.profiler_api.dto.ProfileResponse;
import com.devStephen.profiler_api.dto.ProfileSummary;
import com.devStephen.profiler_api.services.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService  profileService;

    @PostMapping
    public ResponseEntity<?> createProfile (@Valid @RequestBody CreateProfileRequest request){
        Map<String, Object> profile = profileService.createProfile(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<?> getProfile ( @PathVariable String profileId){
        ProfileResponse profile = profileService.getProfile(profileId);
        return ResponseEntity.ok(Map.of("status", "success", "data", profile));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProfile (
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String country_id,
            @RequestParam(required = false) String age_group){
        List<ProfileSummary> profiles = profileService.getAllProfile(gender, country_id, age_group);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("count", profiles.size());
        response.put("data", profiles);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile ( @PathVariable String profileId){
        profileService.deleteProfile(profileId);
        return ResponseEntity.noContent().build();
    }
}

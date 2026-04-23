package com.devStephen.profiler_api.services;

import com.devStephen.profiler_api.client.ExternalClientCall;
import com.devStephen.profiler_api.dto.ProfileResponse;
import com.devStephen.profiler_api.dto.ProfileSummary;
import com.devStephen.profiler_api.exceptions.BadRequestException;
import com.devStephen.profiler_api.exceptions.NotFoundException;
import com.devStephen.profiler_api.exceptions.UnprocessableException;
import com.devStephen.profiler_api.model.Profile;
import com.devStephen.profiler_api.repository.ProfileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ExternalClientCall clientCall;
    private final ProfileRepo profileRepo;

    public Map<String, Object> createProfile(String name) {


        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Missing or empty name");
        }

        if (!name.matches("^[a-zA-Z]+$")) {
            throw new UnprocessableException("Invalid type");
        }

        String nameToLowerCase = name.trim().toLowerCase();

        Optional<Profile> existingProfile = profileRepo.findByNameIgnoreCase(nameToLowerCase);

        if (existingProfile.isPresent()) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("status", "success");
            result.put("message", "Profile already exists");
            result.put("data", toResponse(existingProfile.get()));
            return result;
        }

        // Call all three APIs
        Map<String, Object> genderData = clientCall.fetchGender(nameToLowerCase);
        Map<String, Object> ageData = clientCall.fetchAge(nameToLowerCase);
        Map<String, Object> nationalityData = clientCall.fetchNationality(nameToLowerCase);


        // Pick highest probability country
        List<Map<String, Object>> countries = (List<Map<String, Object>>) nationalityData.get("country");
        Map<String, Object> topCountry = getTopCountry(countries);

        // Build and save profile
        Profile profile = Profile.builder()
                .name(nameToLowerCase)
                .gender((String) genderData.get("gender"))
                .sampleSize((Integer) genderData.get("count"))
                .genderProbability((Double) genderData.get("probability"))
                .age(((Number) ageData.get("age")).intValue())
                .countryId((String) topCountry.get("country_id"))
                .countryProbability((Double) nationalityData.get("probability"))
                .ageGroup(classifyAge(((Number) ageData.get("age")).intValue()))
                .build();

//        profile.setCountryId((String) topCountry.get("country_id"));
//        profile.setCountryProbability((Double) topCountry.get("probability"));

        profileRepo.save(profile);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("data", toResponse(profile));


        return response;

    }

    private String classifyAge(int age) {
        if (age <= 12) return "child";
        if (age <= 19) return "teenager";
        if (age <= 59) return "adult";
        return "senior";
    }

    private Map<String, Object> getTopCountry(List<Map<String, Object>> countries) {
        Map<String, Object> top = null;
        double maxProb = -1;
        for (Map<String, Object> country : countries) {
            double prob = ((Number) country.get("probability")).doubleValue();
            if (prob > maxProb) {
                maxProb = prob;
                top = country;
            }
        }
        return top; // just return the map directly
    }

    private ProfileResponse toResponse(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .name(profile.getName())
                .gender(profile.getGender())
                .genderProbability(profile.getGenderProbability())
                .age(profile.getAge())
                .sampleSize(profile.getSampleSize())
                .ageGroup(profile.getAgeGroup())
                .countryId(profile.getCountryId())
                .countryProbability(profile.getCountryProbability())
                .createdAt(profile.getCreatedAt())
                .build();

    }


    public ProfileResponse getProfile(UUID profileId) {
        Optional<Profile> existingProfile = profileRepo.findById(profileId);

        if (existingProfile.isPresent()) {
            return toResponse(existingProfile.get());

        }
        throw new NotFoundException("Profile not found for profile id: " + profileId);
    }


    public List<ProfileSummary> getAllProfile(String gender, String countryId, String ageGroup) {

    /*
    Fetch all profiles from DB
    Filter by gender, countryId, ageGroup only if they were passed — if not passed, ignore that filter
    Convert each Profile to a ProfileSummary
    Return the list
     */

        List<Profile> allProfiles = profileRepo.findAll();

        return allProfiles.stream()
                .filter(p -> gender == null || p.getGender().equalsIgnoreCase(gender))
                .filter(p -> countryId == null || p.getCountryId().equalsIgnoreCase(countryId))
                .filter(p -> ageGroup == null || p.getAgeGroup().equalsIgnoreCase(ageGroup))
                .map(a -> toSummary(a))
                .toList();

    }

    private ProfileSummary toSummary(Profile profile) {
        return ProfileSummary.builder()
                .name(profile.getName())
                .gender(profile.getGender())
                .age(profile.getAge())
                .ageGroup(profile.getAgeGroup())
                .countryId(profile.getCountryId())
                .build();
    }


    public void deleteProfile(UUID profileId) {
        profileRepo.deleteById(profileId);
    }
}

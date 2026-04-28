package com.devStephen.profiler_api.services;

import static com.devStephen.profiler_api.util.CountryMapper.COUNTRY_NAMES;

import com.devStephen.profiler_api.client.ExternalClientCall;
import com.devStephen.profiler_api.dto.ProfileResponse;
import com.devStephen.profiler_api.dto.ProfileSummary;
import com.devStephen.profiler_api.exceptions.BadRequestException;
import com.devStephen.profiler_api.exceptions.NotFoundException;
import com.devStephen.profiler_api.exceptions.UnprocessableException;
import com.devStephen.profiler_api.model.Profile;
import com.devStephen.profiler_api.repository.ProfileRepo;
import com.devStephen.profiler_api.repository.ProfileSpecification;
import com.devStephen.profiler_api.util.NaturalLanguageParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

  private final ExternalClientCall clientCall;
  private final ProfileRepo profileRepo;
  private final ProfileSpecification profileSpecification;

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

      log.info("Profile already exists for name: {}", nameToLowerCase);

      Map<String, Object> result = new LinkedHashMap<>();
      result.put("status", "success");
      result.put("message", "Profile already exists");
      result.put("data", toResponse(existingProfile.get()));
      return result;
    }

    log.info("Creating new profile for name: {}", nameToLowerCase);

    // APIs call
    Map<String, Object> genderData = clientCall.fetchGender(nameToLowerCase);
    Map<String, Object> ageData = clientCall.fetchAge(nameToLowerCase);
    Map<String, Object> nationalityData = clientCall.fetchNationality(nameToLowerCase);

    // Pick highest probability country
    List<Map<String, Object>> countries =
        (List<Map<String, Object>>) nationalityData.get("country");
    Map<String, Object> topCountry = getTopCountry(countries);

    String countryCode = (String) topCountry.get("country_id");
    String countryName = COUNTRY_NAMES.getOrDefault(countryCode, countryCode);

    // Build and save profile
    Profile profile =
        Profile.builder()
            .name(nameToLowerCase)
            .gender((String) genderData.get("gender"))
            .genderProbability((Double) genderData.get("probability"))
            .age(((Number) ageData.get("age")).intValue())
            .countryId((String) topCountry.get("country_id"))
            .countryName(countryName)
            .countryProbability((Double) topCountry.get("probability"))
            .ageGroup(classifyAge(((Number) ageData.get("age")).intValue()))
            .build();

    profileRepo.save(profile);
    log.info("Profile saved with id: {}", profile.getId());

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

  public ProfileResponse getProfile(UUID profileId) {
    log.info("Fetching profile with id: {}", profileId);
    Optional<Profile> existingProfile = profileRepo.findById(profileId);

    if (existingProfile.isPresent()) {
      return toResponse(existingProfile.get());
    }
    throw new NotFoundException("Profile not found for profile id: " + profileId);
  }

  public Map<String, Object> getAllProfile(
      String gender,
      String countryId,
      String ageGroup,
      Integer minAge,
      Integer maxAge,
      Double minGenderProbability,
      Double minCountryProbability,
      String sortBy,
      String order,
      int page,
      int limit) {

    // Validate limit
    if (limit > 50) limit = 50;
    if (page < 1) page = 1;

    // Validate sortBy field
    List<String> validSortFields = List.of("age", "created_at", "gender_probability");
    String resolvedSortBy = "createdAt"; // default
    if (sortBy != null) {
      if (sortBy.equals("age")) resolvedSortBy = "age";
      else if (sortBy.equals("created_at")) resolvedSortBy = "createdAt";
      else if (sortBy.equals("gender_probability")) resolvedSortBy = "genderProbability";
      else throw new BadRequestException("Invalid sort_by value. Allowed: " + validSortFields);
    }

    // Validate order
    Sort.Direction direction = Sort.Direction.ASC;
    if (order != null) {
      if (order.equalsIgnoreCase("desc")) direction = Sort.Direction.DESC;
      else if (!order.equalsIgnoreCase("asc")) {
        throw new BadRequestException("Invalid order value. Allowed: asc, desc");
      }
    }
    /*
    Fetch all profiles from DB
    Filter by gender, countryId, ageGroup only if they were passed — if not passed, ignore that filter
    Convert each Profile to a ProfileSummary
    Return the list
     */

    Sort sort = Sort.by(direction, resolvedSortBy);
    Pageable pageable = PageRequest.of(page - 1, limit, sort);

    Specification<Profile> spec =
        ProfileSpecification.withFilters(
            gender,
            countryId,
            ageGroup,
            minAge,
            maxAge,
            minGenderProbability,
            minCountryProbability);

    Page<Profile> result = profileRepo.findAll(spec, pageable);

    log.info(
        "getAllProfiles returned {} results (page {}, limit {})",
        result.getTotalElements(),
        page,
        limit);

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("status", "success");
    response.put("page", page);
    response.put("limit", limit);
    response.put("total", result.getTotalElements());
    response.put("data", result.getContent().stream().map(this::toResponse).toList());
    return response;
  }

  public void deleteProfile(UUID profileId) {
    if (!profileRepo.existsById(profileId)) {
      throw new NotFoundException("Profile not found for profile id: " + profileId);
    }
    profileRepo.deleteById(profileId);
  }

  private ProfileResponse toResponse(Profile profile) {
    return ProfileResponse.builder()
        .id(profile.getId())
        .name(profile.getName())
        .gender(profile.getGender())
        .genderProbability(profile.getGenderProbability())
        .age(profile.getAge())
        .ageGroup(profile.getAgeGroup())
        .countryId(profile.getCountryId())
        .countryName(profile.getCountryName())
        .countryProbability(profile.getCountryProbability())
        .createdAt(profile.getCreatedAt())
        .build();
  }

  public Map<String, Object> searchProfiles(String q, int page, int limit) {

    if (q == null || q.isBlank()) {
      throw new BadRequestException("Missing or empty query");
    }

    NaturalLanguageParser.ParsedQuery parsed = NaturalLanguageParser.parse(q);
    log.info("Parsed query '{}' → {}", q, parsed);

    if (!parsed.isInterpreted()) {
      Map<String, Object> error = new LinkedHashMap<>();
      error.put("status", "error");
      error.put("message", "Unable to interpret query");
      return error;
    }

    if (limit > 50) limit = 50;
    if (page < 1) page = 1;

    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.ASC, "createdAt"));

    Specification<Profile> spec =
        ProfileSpecification.withFilters(
            parsed.getGender(),
            parsed.getCountryId(),
            parsed.getAgeGroup(),
            parsed.getMinAge(),
            parsed.getMaxAge(),
            null,
            null);

    Page<Profile> result = profileRepo.findAll(spec, pageable);

    log.info("searchProfiles returned {} results for query '{}'", result.getTotalElements(), q);

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("status", "success");
    response.put("page", page);
    response.put("limit", limit);
    response.put("total", result.getTotalElements());
    response.put("data", result.getContent().stream().map(this::toResponse).toList());
    return response;
  }
}

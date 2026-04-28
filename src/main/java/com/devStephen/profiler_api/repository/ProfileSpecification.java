package com.devStephen.profiler_api.repository;

import com.devStephen.profiler_api.model.Profile;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProfileSpecification {

  public static Specification<Profile> withFilters(
      String gender,
      String countryId,
      String ageGroup,
      Integer minAge,
      Integer maxAge,
      Double minGenderProbability,
      Double minCountryProbability
  ) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (gender != null && !gender.isBlank()) {
        predicates.add(criteriaBuilder.equal(
            criteriaBuilder.lower(root.get("gender")),
            gender.toLowerCase()
        ));
      }

      if (countryId != null && !countryId.isBlank()) {
        predicates.add(criteriaBuilder.equal(
            criteriaBuilder.lower(root.get("countryId")),
            countryId.toLowerCase()
        ));
      }

      if (ageGroup != null && !ageGroup.isBlank()) {
        predicates.add(criteriaBuilder.equal(
            criteriaBuilder.lower(root.get("ageGroup")),
            ageGroup.toLowerCase()
        ));
      }

      if (minAge != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(
            root.get("age"), minAge
        ));
      }

      if (maxAge != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(
            root.get("age"), maxAge
        ));
      }

      if (minGenderProbability != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(
            root.get("genderProbability"), minGenderProbability
        ));
      }

      if (minCountryProbability != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(
            root.get("countryProbability"), minCountryProbability
        ));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
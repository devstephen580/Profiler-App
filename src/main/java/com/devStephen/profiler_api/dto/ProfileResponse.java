package com.devStephen.profiler_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private String id;
    private String name;
    private String gender;
    private Double genderProbability;
    private Integer sampleSize;
    private Integer age;
    private String ageGroup;
    private String countryId;
    private Double countryProbability;
    private Instant createdAt;
}

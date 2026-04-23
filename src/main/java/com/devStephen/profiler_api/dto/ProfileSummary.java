package com.devStephen.profiler_api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProfileSummary {
    private UUID id;
    private String name;
    private String gender;
    private Integer age;
    private String ageGroup;
    private String countryId;
}

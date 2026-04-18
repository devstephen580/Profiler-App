package com.devStephen.profiler_api.dto;

import lombok.Data;

@Data
public class ProfileSummary {
    private String id;
    private String name;
    private String gender;
    private Integer age;
    private String ageGroup;
    private String countryId;
}

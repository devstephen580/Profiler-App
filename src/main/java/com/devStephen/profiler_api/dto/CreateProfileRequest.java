package com.devStephen.profiler_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class CreateProfileRequest {

    @NotBlank(message = "Name is required")
    private String name;


}

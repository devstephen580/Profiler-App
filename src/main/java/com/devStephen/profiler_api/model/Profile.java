package com.devStephen.profiler_api.model;

import jakarta.persistence.*;

import java.time.Instant;

public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String name;

    private String gender;

    @Column(name = "gender_probability")
    private Double genderProbability;

    @Column(name = "sample_size")
    private Integer sampleSize;

    private Integer age;

    @Column(name = "age_group")
    private String ageGroup;

    @Column(name = "country_id")
    private String countryId;

    @Column(name = "country_probability")
    private Double countryProbability;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist // Timezone aware, Yes — always UTC
    public void prePersist() {
        this.createdAt = Instant.now();
    }

}

package com.devStephen.profiler_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
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

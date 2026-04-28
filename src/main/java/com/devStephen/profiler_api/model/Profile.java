package com.devStephen.profiler_api.model;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "profile", indexes = {
        @Index(name = "idx_gender", columnList = "gender"),
        @Index(name = "idx_country_id", columnList = "country_id"),
        @Index(name = "idx_age_group", columnList = "age_group"),
        @Index(name = "idx_age", columnList = "age")})
public class Profile {
    @Id
    private UUID id;

    @PrePersist
    public void prePersist(){
        if (this.id == null) {
            this.id = Generators.timeBasedEpochGenerator().generate();

        }
        this.createdAt = Instant.now();
    }

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    @Column(nullable = false)
    private String gender;

    @Column(name = "gender_probability", nullable = false)
    private Double genderProbability;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "age_group", nullable = false)
    private String ageGroup;

    @Column(name = "country_id", length = 2, nullable = false)
    private String countryId;

    @Column(name = "country_probability")
    private Double countryProbability;

// chore: update Profile entity schema, add indexes, and remove sample_size field
}

package com.devStephen.profiler_api.repository;

import com.devStephen.profiler_api.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepo extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByNameIgnoreCase(String lowerCase);
}

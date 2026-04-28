package com.devStephen.profiler_api.repository;

import com.devStephen.profiler_api.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepo extends JpaRepository<Profile, UUID>,
    JpaSpecificationExecutor<Profile> {

  Optional<Profile> findByNameIgnoreCase(String name);
}

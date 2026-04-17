package com.devStephen.profiler_api.repository;

import com.devStephen.profiler_api.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepo extends JpaRepository<Profile, String> {
}

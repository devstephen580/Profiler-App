package com.devStephen.profiler_api.seeder;

import com.devStephen.profiler_api.model.Profile;
import com.devStephen.profiler_api.repository.ProfileRepo;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileSeeder implements ApplicationRunner {

    private final ProfileRepo profileRepo;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        long count = profileRepo.count();

        if (count > 0) {
            log.info("Database already seeded with {} profiles. Skipping.", count);
            return;
        }

        log.info("Starting database seeding...");

        InputStream inputStream = new ClassPathResource("seed_profiles.json").getInputStream();
        JsonNode root = objectMapper.readTree(inputStream);
        JsonNode profiles = root.get("profiles");

        int saved = 0;
        for (JsonNode node : profiles) {
            String name = node.get("name").asText().trim().toLowerCase();

            // Skip if profile already exists (safety check)
            if (profileRepo.findByNameIgnoreCase(name).isPresent()) {
                log.debug("Skipping duplicate profile: {}", name);
                continue;
            }

            Profile profile = Profile.builder()
                    .name(name)
                    .gender(node.get("gender").asText())
                    .genderProbability(node.get("gender_probability").asDouble())
                    .age(node.get("age").asInt())
                    .ageGroup(node.get("age_group").asText())
                    .countryId(node.get("country_id").asText())
                    .countryName(node.get("country_name").asText())
                    .countryProbability(node.get("country_probability").asDouble())
                    .build();

            profileRepo.save(profile);
            saved++;
        }

        log.info("Seeding complete. {} profiles inserted.", saved);
    }
}

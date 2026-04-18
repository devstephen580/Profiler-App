package com.devStephen.profiler_api.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExternalClientCall {

    private final WebClient webClient;

    public ResponseEntity<?> fetchGender(String name) {

        String url = "https://api.genderize.io?name=" + name;

        Map response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null
                || response.get("gender") == null
                || response.get("count") == null
                || ((Number) response.get("count")).intValue() == 0) {
            throw new RuntimeException("Genderize returned an invalid response");
        }
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<?> fetchAge(String name) {

        String url = "https://api.agify.io?name=" + name;

        Map response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null
                || response.get("gender") == null
                || response.get("age") == null
                || ((Number) response.get("age")).intValue() == 0) {
            throw new RuntimeException("Agify returned an invalid response");
        }
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<?> fetchNationality(String name) {

        String url = "https://api.nationalize.io?name=" + name;

        Map response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null
                || response.get("gender") == null
                || response.get("age") == null
                || ((Number) response.get("age")).intValue() == 0) {
            throw new RuntimeException("Nationalize returned an invalid response");
        }
        return ResponseEntity.ok().body(response);
    }

}

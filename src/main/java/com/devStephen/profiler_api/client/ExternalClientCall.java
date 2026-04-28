package com.devStephen.profiler_api.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExternalClientCall {

    private final WebClient webClient;

    public Map<String, Object> fetchGender(String name) {

        String url = "https://api.genderize.io?name=" + name;

        Map response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null
                || response.get("gender") == null) {
            throw new RuntimeException("Genderize returned an invalid response");
        }
        return response;
    }

    public Map<String, Object> fetchAge(String name) {

        String url = "https://api.agify.io?name=" + name;

        Map response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null
                || response.get("age") == null
                || ((Number) response.get("age")).intValue() == 0) {
            throw new RuntimeException("Agify returned an invalid response");
        }
        return response;
    }

    public Map<String, Object> fetchNationality(String name) {

        String url = "https://api.nationalize.io?name=" + name;

        Map response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null
                || response.get("country") == null) {
            throw new RuntimeException("Nationalize returned an invalid response");
        }
        return response;
    }

}

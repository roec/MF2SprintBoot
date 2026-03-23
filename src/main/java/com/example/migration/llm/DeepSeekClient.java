package com.example.migration.llm;

import com.example.migration.config.DeepSeekProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class DeepSeekClient {

    private final RestClient restClient;
    private final DeepSeekProperties properties;

    public DeepSeekClient(RestClient restClient, DeepSeekProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public String complete(String prompt) {
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            return "[DRY-RUN] DeepSeek API key missing. Prompt accepted for offline generation.";
        }

        Map<String, Object> payload = Map.of(
                "model", properties.model(),
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a semantic migration engine."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.1
        );

        DeepSeekChatResponse response = restClient.post()
                .uri(properties.baseUrl() + "/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + properties.apiKey())
                .body(payload)
                .retrieve()
                .body(DeepSeekChatResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return "[EMPTY] DeepSeek returned no choices.";
        }
        return response.choices().getFirst().message().content();
    }

    public record DeepSeekChatResponse(List<Choice> choices) {
    }

    public record Choice(Message message) {
    }

    public record Message(String content) {
    }
}

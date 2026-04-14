package kr.dgucaps.caps.domain.report.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DiscordWebhookSender {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${discord.webhook.uri}")
    private String webhookUri;

    @Value("${discord.webhook.name:CAPS Report}")
    private String webhookName;

    public void send(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "username", webhookName,
                "content", truncate(message, 1800)
        );

        restTemplate.postForEntity(webhookUri, new HttpEntity<>(body, headers), String.class);
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max - 3) + "...";
    }
}

package com.app.email_ai_writer.service;

import com.app.email_ai_writer.entity.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorService {

    private final WebClient webClient;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateEmailReply(EmailRequest emailReq) {
        // Build the prompt
        String prompt = buildPrompt(emailReq);
        // Craft a request
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text", prompt)
                        })
                });
        // Do request and get response
        try {
            String response = webClient.post()
                    .uri(geminiApiUrl + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            // Extract response and Return
            return extractResponseContent(response);
        } catch (Exception e) {
            e.printStackTrace(); // Log error for local debugging
            return "Error generating reply: " + e.getMessage(); // Return friendly error
        }
    }

    private String extractResponseContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);

            // Check for candidates
            if (!rootNode.has("candidates") || rootNode.path("candidates").isEmpty()) {
                return "Error: No candidates returned from AI. raw response: " + response;
            }

            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailReq) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(
                "Generate a professional email reply for the following email content. Please don't generate a subject line");
        if (emailReq.getTone() != null && !emailReq.getTone().isEmpty()) {
            prompt.append("Use a ").append(emailReq.getTone()).append(" tone. ");
        }
        if (emailReq.getReplyHints() != null && !emailReq.getReplyHints().isEmpty()) {
            prompt.append("\nKey points to include: ").append(emailReq.getReplyHints());
        }
        prompt.append("\nOriginal email: \n").append(emailReq.getEmailContent());
        return prompt.toString();
    }
}

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

    public String generateEmailReply(EmailRequest emailReq){
        //Build the prompt
        String prompt=buildPrompt(emailReq);
        //Craft a request
        Map<String,Object> requestBody=Map.of(
                "contents",new Object[]{
                       Map.of("parts", new Object[]{
                               Map.of("text",prompt)
                       })
                }
        );
        //Do request and get response
        String response=webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        //Extract response and Return
        return extactResponseContent(response);


    }

    private String extactResponseContent(String response) {
        try {
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode=mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildPrompt(EmailRequest emailReq) {
        StringBuilder prompt=new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content. Please don't generate a subject line");
        if(emailReq.getTone()!=null && !emailReq.getTone().isEmpty()){
            prompt.append("Use a").append(emailReq.getTone()).append(" tone. ");
        }
        prompt.append("\nOriginal email: \n").append(emailReq.getEmailContent());
        return prompt.toString();
    }
}

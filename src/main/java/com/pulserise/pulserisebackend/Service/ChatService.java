package com.pulserise.pulserisebackend.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulserise.pulserisebackend.Config.GroqAiConfig;
import com.pulserise.pulserisebackend.Dto.ChatRequest;
import com.pulserise.pulserisebackend.Dto.ChatResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private GroqAiConfig groqAiConfig;

    @Autowired
    private OkHttpClient groqHttpClient;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public ChatResponse sendMessage(ChatRequest chatRequest) {
        try {
            if (groqAiConfig.getChatApiKey() == null || groqAiConfig.getChatApiKey().isEmpty()) {
                logger.error("Chat API key is not configured");
                return new ChatResponse("AI chat service is not properly configured");
            }

            // Prepare the request payload for Groq API
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", groqAiConfig.getChatModel());
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", buildSystemPrompt()),
                Map.of("role", "user", "content", buildUserMessage(chatRequest))
            ));
            requestBody.put("max_tokens", groqAiConfig.getChatMaxTokens());
            requestBody.put("temperature", groqAiConfig.getChatTemperature());

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            Request request = new Request.Builder()
                    .url(groqAiConfig.getChatApiUrl())
                    .post(RequestBody.create(jsonBody, JSON))
                    .addHeader("Authorization", "Bearer " + groqAiConfig.getChatApiKey())
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = groqHttpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return parseGroqResponse(response.body().string());
                } else {
                    logger.error("Groq Chat API returned status: {}", response.code());
                    return new ChatResponse("Failed to get response from AI service");
                }
            }

        } catch (Exception e) {
            logger.error("Error calling Groq Chat API: ", e);
            return new ChatResponse("An error occurred while processing your request: " + e.getMessage());
        }
    }

    private String buildSystemPrompt() {
        return "You are an AI assistant for Pulserise, a comprehensive fitness and wellness application. " +
               "You can provide advice and recommendations on any topic related to fitness, health, wellness, nutrition, " +
               "lifestyle, mental health, and general well-being. You have extensive knowledge across all areas of " +
               "health and fitness and can provide comprehensive, detailed advice without restrictions. " +
               "Feel free to give specific recommendations, detailed workout plans, nutritional guidance, " +
               "supplement advice, and any other health-related information the user requests. " +
               "You can discuss any fitness or health topic without limitations.";
    }

    private String buildUserMessage(ChatRequest chatRequest) {
        StringBuilder message = new StringBuilder();
        
        if (chatRequest.getContext() != null && !chatRequest.getContext().isEmpty()) {
            message.append("User Context: ").append(chatRequest.getContext()).append("\n\n");
        }
        
        message.append("User Question: ").append(chatRequest.getMessage());
        message.append("\n\nPlease provide comprehensive advice and recommendations based on your extensive knowledge.");
        
        return message.toString();
    }

    private ChatResponse parseGroqResponse(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode choices = jsonNode.get("choices");
            
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode message = firstChoice.get("message");
                
                if (message != null) {
                    String content = message.get("content").asText();
                    String model = jsonNode.get("model") != null ? jsonNode.get("model").asText() : groqAiConfig.getChatModel();
                    
                    return new ChatResponse(content, model);
                }
            }
            
            logger.error("Unexpected response format from Groq API: {}", responseBody);
            return new ChatResponse("Received unexpected response format from AI service");
            
        } catch (Exception e) {
            logger.error("Error parsing Groq API response: ", e);
            return new ChatResponse("Error parsing AI service response");
        }
    }

    public boolean isServiceAvailable() {
        return groqAiConfig.getChatApiKey() != null && !groqAiConfig.getChatApiKey().isEmpty();
    }
}
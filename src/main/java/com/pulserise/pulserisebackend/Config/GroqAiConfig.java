package com.pulserise.pulserisebackend.Config;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class GroqAiConfig {

    // Chat Service Configuration
    @Value("${groq.chat.api.key:}")
    private String chatApiKey;

    @Value("${groq.chat.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String chatApiUrl;

    @Value("${groq.chat.api.model:llama3-8b-8192}")
    private String chatModel;

    @Value("${groq.chat.api.timeout:30}")
    private int chatTimeoutSeconds;

    @Value("${groq.chat.api.maxTokens:1000}")
    private int chatMaxTokens;

    @Value("${groq.chat.api.temperature:0.7}")
    private double chatTemperature;

    // Recommendation Service Configuration
    @Value("${groq.recommendation.api.key:}")
    private String recommendationApiKey;

    @Value("${groq.recommendation.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String recommendationApiUrl;

    @Value("${groq.recommendation.api.model:llama3-8b-8192}")
    private String recommendationModel;

    @Value("${groq.recommendation.api.timeout:30}")
    private int recommendationTimeoutSeconds;

    @Value("${groq.recommendation.api.maxTokens:1500}")
    private int recommendationMaxTokens;

    @Value("${groq.recommendation.api.temperature:0.8}")
    private double recommendationTemperature;

    // Legacy configuration (for backward compatibility)
    @Value("${groq.api.key:}")
    private String apiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    @Value("${groq.api.model:llama3-8b-8192}")
    private String model;

    @Value("${groq.api.timeout:30}")
    private int timeoutSeconds;

    @Bean
    public OkHttpClient groqHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(Math.max(chatTimeoutSeconds, recommendationTimeoutSeconds)))
                .readTimeout(Duration.ofSeconds(Math.max(chatTimeoutSeconds, recommendationTimeoutSeconds)))
                .writeTimeout(Duration.ofSeconds(Math.max(chatTimeoutSeconds, recommendationTimeoutSeconds)))
                .build();
    }

    // Chat Service Getters
    public String getChatApiKey() {
        return chatApiKey != null && !chatApiKey.isEmpty() ? chatApiKey : apiKey;
    }

    public String getChatApiUrl() {
        return chatApiUrl;
    }

    public String getChatModel() {
        return chatModel;
    }

    public int getChatTimeoutSeconds() {
        return chatTimeoutSeconds;
    }

    public int getChatMaxTokens() {
        return chatMaxTokens;
    }

    public double getChatTemperature() {
        return chatTemperature;
    }

    // Recommendation Service Getters
    public String getRecommendationApiKey() {
        return recommendationApiKey != null && !recommendationApiKey.isEmpty() ? recommendationApiKey : apiKey;
    }

    public String getRecommendationApiUrl() {
        return recommendationApiUrl;
    }

    public String getRecommendationModel() {
        return recommendationModel;
    }

    public int getRecommendationTimeoutSeconds() {
        return recommendationTimeoutSeconds;
    }

    public int getRecommendationMaxTokens() {
        return recommendationMaxTokens;
    }

    public double getRecommendationTemperature() {
        return recommendationTemperature;
    }

    // Legacy getters (for backward compatibility)
    public String getApiKey() {
        return apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getModel() {
        return model;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }
}
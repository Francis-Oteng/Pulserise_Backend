package com.pulserise.pulserisebackend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS configuration is now handled in SecurityConfig.java
    // to avoid duplicate configurations
}

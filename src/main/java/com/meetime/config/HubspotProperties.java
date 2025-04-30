package com.meetime.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "hubspot")
public class HubspotProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authUrl;
    private String tokenUrl;
    private String scopes;
    private Api api;

    @Data
    public static class Api {
        private String baseUrl;
        private String contactsPath;
    }
}
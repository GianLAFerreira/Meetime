package com.meetime.integration.oauth.client;

import com.meetime.config.HubspotProperties;
import com.meetime.integration.oauth.model.response.OAuthCallBackClientResponse;
import com.meetime.integration.oauth.model.response.OAuthGenerateAuthorizationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientOAuthClient implements OAuthClient {

    private final WebClient webClient;
    private final HubspotProperties hubspotProps;

    @Override
    public OAuthGenerateAuthorizationResponse generateAuthorizationUrl() {

        String url = hubspotProps.getAuthUrl()
                + "?client_id=" + hubspotProps.getClientId()
                + "&redirect_uri=" + URLEncoder.encode(hubspotProps.getRedirectUri(), StandardCharsets.UTF_8)
                + "&scope=" + URLEncoder.encode(hubspotProps.getScopes(), StandardCharsets.UTF_8)
                + "&response_type=code";

        return new OAuthGenerateAuthorizationResponse(url);
    }

    @Override
    public Mono<OAuthCallBackClientResponse> exchangeCodeForToken(String code) {
        return webClient.post()
                .uri(hubspotProps.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(
                        "grant_type=authorization_code" +
                                "&client_id=" + hubspotProps.getClientId() +
                                "&client_secret=" + hubspotProps.getClientSecret() +
                                "&redirect_uri=" + URLEncoder.encode(hubspotProps.getRedirectUri(), StandardCharsets.UTF_8) +
                                "&code=" + code
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
}

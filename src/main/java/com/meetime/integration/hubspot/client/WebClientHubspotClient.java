package com.meetime.integration.hubspot.client;

import com.meetime.config.HubspotProperties;
import com.meetime.integration.hubspot.model.request.CreateContactHubsPotRequest;
import com.meetime.integration.hubspot.model.response.CreateContactHubsPotResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientHubspotClient implements HubspotClient {

    private final WebClient webClient;
    private final HubspotProperties hubspotProps;

    @Override
    public Mono<CreateContactHubsPotResponse> createContact(String authorization, CreateContactHubsPotRequest properties) {
        log.info("Client → POST /contacts payload: {}", properties);
        return webClient.post()
                .uri(hubspotProps.getApi().getBaseUrl() + hubspotProps.getApi().getContactsPath())
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(Map.of("properties", properties))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Mono<Map<String, Object>> listContacts(String authorization) {
        log.info("Client → GET /contacts");
        return webClient.get()
                .uri(hubspotProps.getApi().getBaseUrl() + hubspotProps.getApi().getContactsPath())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authorization)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
}
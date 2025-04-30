package com.meetime.integration.hubspot.client;

import com.meetime.integration.hubspot.model.request.CreateContactHubsPotRequest;
import com.meetime.integration.hubspot.model.response.CreateContactHubsPotResponse;
import reactor.core.publisher.Mono;
import java.util.Map;

public interface HubspotClient {

    Mono<CreateContactHubsPotResponse> createContact(String authorization, CreateContactHubsPotRequest properties);

    Mono<Map<String, Object>> listContacts(String authorization);
}

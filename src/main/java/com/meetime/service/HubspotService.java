package com.meetime.service;

import com.meetime.controller.model.request.CreateContactRequest;
import com.meetime.controller.model.response.CreateContactResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface HubspotService {

    Mono<CreateContactResponse> createContact(CreateContactRequest contactData);
    Mono<Map<String, Object>> listContacts();
}

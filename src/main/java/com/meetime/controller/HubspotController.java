package com.meetime.controller;

import com.meetime.controller.model.request.CreateContactRequest;
import com.meetime.controller.model.response.CreateContactResponse;
import com.meetime.service.HubspotService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/hubspot")
@AllArgsConstructor
public class HubspotController {

    private final HubspotService hubspotService;

    @PostMapping("/contacts")
    public Mono<CreateContactResponse> createContact(@RequestBody CreateContactRequest contactData) {
        return hubspotService.createContact(contactData);
    }

    @GetMapping("/contacts")
    public Mono<Map<String, Object>> listContacts() {
        return hubspotService.listContacts();
    }
}

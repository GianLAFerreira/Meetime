package com.meetime.service;

import com.meetime.controller.model.request.CreateContactRequest;
import com.meetime.controller.model.response.CreateContactResponse;
import com.meetime.integration.hubspot.client.HubspotClient;
import com.meetime.integration.hubspot.model.request.CreateContactHubsPotRequest;
import com.meetime.service.mapper.ContactMapper;
import com.meetime.service.vo.CreateContactCreateVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubspotServiceImpl implements HubspotService {

    private final HubspotClient hubspotClient;
    private final HubspotRateLimiter hubspotRateLimiter;
    private final TokenStorageService tokenStorageService;
    private final ContactMapper contactMapper;

    @Override
    public Mono<CreateContactResponse> createContact(CreateContactRequest request) {
        hubspotRateLimiter.acquire();

        String authorization = tokenStorageService.getAccessToken();

        if (authorization == null) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Access token não encontrado"
            ));
        }

        CreateContactCreateVO createContactCreateVO = contactMapper.toCreateContactCreate(request);
        log.info("CreateContactCreateVO: {}", createContactCreateVO);

        CreateContactHubsPotRequest createContactHubsPotRequest = contactMapper.toCreateContactCreateVO(createContactCreateVO);
        log.info("CreateContactHubsPot: {}", createContactHubsPotRequest);


        return hubspotClient.createContact(authorization, createContactHubsPotRequest)
                .doOnNext(r -> log.info("Service → contato criado (raw): {}", r))
                .doOnError(e -> log.error("Service → erro criando contato", e))
                .map(contactMapper::toResponse);
    }

    @Override
    public Mono<Map<String, Object>> listContacts() {
        String authorization = tokenStorageService.getAccessToken();

        log.info("Listando contatos no HubSpot: ");

        return hubspotClient.listContacts(authorization)
                .doOnNext(response -> log.info("Contatos listados com sucesso. Resposta: {}", response))
                .doOnError(error -> log.error("Erro ao listar contatos no HubSpot", error));
    }
}

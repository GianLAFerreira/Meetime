package com.meetime.service;

import com.meetime.controller.model.request.CreateContactRequest;
import com.meetime.controller.model.response.CreateContactResponse;
import com.meetime.integration.hubspot.client.HubspotClient;
import com.meetime.integration.hubspot.model.request.CreateContactHubsPotRequest;
import com.meetime.integration.hubspot.model.response.CreateContactHubsportProperties;
import com.meetime.integration.hubspot.model.response.CreateContactHubsPotResponse;
import com.meetime.service.mapper.ContactMapper;
import com.meetime.service.vo.CreateContactCreateVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class HubspotServiceImplTest {

    @Mock HubspotClient hubspotClient;
    @Mock HubspotRateLimiter hubspotRateLimiter;
    @Mock TokenStorageService tokenStorageService;
    @Mock ContactMapper contactMapper;

    @InjectMocks HubspotServiceImpl service;

    private CreateContactRequest request;
    private CreateContactCreateVO createVO;
    private CreateContactHubsPotRequest hubRequest;
    private CreateContactHubsPotResponse hubResponse;
    private CreateContactResponse expectedResponse;

    @BeforeEach
    void setUp() {
        // request
        request = new CreateContactRequest();
        request.setEmail("a@b.com");
        request.setFirstname("First");
        request.setLastname("Last");
        request.setPhone("123");
        request.setCompany("Co");

        // VO
        createVO = new CreateContactCreateVO();
        createVO.setEmail("a@b.com");
        createVO.setFirstname("First");
        createVO.setLastname("Last");
        createVO.setPhone("123");
        createVO.setCompany("Co");

        // request para HubSpot client
        hubRequest = new CreateContactHubsPotRequest();
        hubRequest.setEmail("a@b.com");
        hubRequest.setFirstname("First");
        hubRequest.setLastname("Last");
        hubRequest.setPhone("123");
        hubRequest.setCompany("Co");

        // resposta do client HubSpot
        CreateContactHubsportProperties props = new CreateContactHubsportProperties();
        props.setEmail("a@b.com");
        props.setFirstname("First");
        props.setLastname("Last");
        props.setPhone("123");
        props.setCompany("Co");

        hubResponse = new CreateContactHubsPotResponse();
        hubResponse.setId("xyz");
        hubResponse.setProperties(props);

        // resposta final mapeada
        expectedResponse = new CreateContactResponse();
        expectedResponse.setEmail("a@b.com");
        expectedResponse.setFirstname("First");
        expectedResponse.setLastname("Last");
        expectedResponse.setPhone("123");
        expectedResponse.setCompany("Co");
    }
    @Test
    void createContact_shouldErrorWhenNoToken() {
        given(tokenStorageService.getAccessToken()).willReturn(null);

        StepVerifier.create(service.createContact(request))
                .expectErrorSatisfies(err -> {
                    assert err instanceof ResponseStatusException;
                    ResponseStatusException ex = (ResponseStatusException) err;
                    assert ex.getStatusCode() == HttpStatus.UNAUTHORIZED;
                })
                .verify();

        then(hubspotRateLimiter).should().acquire();
    }


    @Test
    void createContact_successfulFlow() {
        given(tokenStorageService.getAccessToken()).willReturn("token");
        willDoNothing().given(hubspotRateLimiter).acquire();
        given(contactMapper.toCreateContactCreate(request)).willReturn(createVO);
        given(contactMapper.toCreateContactCreateVO(createVO)).willReturn(hubRequest);
        given(hubspotClient.createContact("token", hubRequest))
                .willReturn(Mono.just(hubResponse));
        given(contactMapper.toResponse(hubResponse)).willReturn(expectedResponse);

        StepVerifier.create(service.createContact(request))
                .assertNext(resp -> resp.equals(expectedResponse))
                .verifyComplete();

        then(hubspotRateLimiter).should().acquire();
        then(contactMapper).should().toCreateContactCreate(request);
        then(contactMapper).should().toCreateContactCreateVO(createVO);
        then(hubspotClient).should().createContact("token", hubRequest);
    }

    @Test
    void listContacts_successful() {
        Map<String, Object> dummy = Map.of("key", "value");
        given(tokenStorageService.getAccessToken()).willReturn("token");
        given(hubspotClient.listContacts("token")).willReturn(Mono.just(dummy));

        StepVerifier.create(service.listContacts())
                .expectNext(dummy)
                .verifyComplete();

        then(hubspotClient).should().listContacts("token");
    }
}

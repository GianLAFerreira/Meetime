package com.meetime.service;

import com.meetime.controller.model.response.OAuthAutorizationResponse;
import com.meetime.controller.model.response.OAuthCallBackResponse;
import com.meetime.integration.oauth.client.OAuthClient;
import com.meetime.integration.oauth.model.response.OAuthCallBackClientResponse;
import com.meetime.integration.oauth.model.response.OAuthGenerateAuthorizationResponse;
import com.meetime.service.mapper.OAuthMappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OAuthServiceImplTest {

    @Mock
    private TokenStorageService tokenStorageService;
    @Mock
    private OAuthClient oAuthClient;
    @Mock
    private OAuthMappers oAuthMappers;

    @InjectMocks
    private OAuthServiceImpl service;

    private OAuthGenerateAuthorizationResponse generateResp;
    private OAuthAutorizationResponse expectedAuthResponse;

    private OAuthCallBackClientResponse oAuthCallBackClientResponse;
    private OAuthCallBackResponse expectedCallbackResponse;

    @BeforeEach
    void setUp() {
        generateResp = new OAuthGenerateAuthorizationResponse("url");
        generateResp.setUrl("https://auth.url");
        expectedAuthResponse = new OAuthAutorizationResponse();
        expectedAuthResponse.setUrl("https://auth.url");

        oAuthCallBackClientResponse = new OAuthCallBackClientResponse();
        oAuthCallBackClientResponse.setAccess_token("abc123");
        oAuthCallBackClientResponse.setRefresh_token("ref123");
        expectedCallbackResponse = new OAuthCallBackResponse();
        expectedCallbackResponse.setAccess_token("abc123");
        expectedCallbackResponse.setRefresh_token("ref123");
    }

    @Test
    void generateAuthorizationUrl_shouldReturnMappedResponse() {
        given(oAuthClient.generateAuthorizationUrl()).willReturn(generateResp);
        given(oAuthMappers.toResponse(generateResp)).willReturn(expectedAuthResponse);

        OAuthAutorizationResponse resp = service.generateAuthorizationUrl();

        assertNotNull(resp);
        assertEquals(expectedAuthResponse, resp);
        then(oAuthClient).should().generateAuthorizationUrl();
        then(oAuthMappers).should().toResponse(generateResp);
        then(tokenStorageService).shouldHaveNoInteractions();
    }

    @Test
    void exchangeCodeForToken_shouldSaveTokenAndReturnMappedMono() {
        String code = "the-code";
        given(oAuthClient.exchangeCodeForToken(code)).willReturn(Mono.just(oAuthCallBackClientResponse));
        given(oAuthMappers.toOAuthCallBackClientResponse(oAuthCallBackClientResponse)).willReturn(expectedCallbackResponse);

        StepVerifier.create(service.exchangeCodeForToken(code))
                .expectNext(expectedCallbackResponse)
                .verifyComplete();

        then(oAuthClient).should().exchangeCodeForToken(code);
        then(tokenStorageService).should().saveAccessToken("abc123");
        then(oAuthMappers).should().toOAuthCallBackClientResponse(oAuthCallBackClientResponse);
    }
}

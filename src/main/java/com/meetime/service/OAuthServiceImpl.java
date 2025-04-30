package com.meetime.service;

import com.meetime.controller.model.response.OAuthAutorizationResponse;
import com.meetime.controller.model.response.OAuthCallBackResponse;
import com.meetime.integration.oauth.client.OAuthClient;
import com.meetime.integration.oauth.model.response.OAuthGenerateAuthorizationResponse;
import com.meetime.service.mapper.OAuthMappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final TokenStorageService tokenStorageService;
    private final OAuthClient oAuthClient;
    private final OAuthMappers oAuthMappers;

    @Override
    public OAuthAutorizationResponse generateAuthorizationUrl() {
        OAuthGenerateAuthorizationResponse oAuthGenerateAuthorizationResponse = oAuthClient.generateAuthorizationUrl();
        log.info("OAuthGenerateAuthorizationResponse: {}", oAuthGenerateAuthorizationResponse);

        return oAuthMappers.toResponse(oAuthGenerateAuthorizationResponse);
    }

    @Override
    public Mono<OAuthCallBackResponse> exchangeCodeForToken(String code) {
        return oAuthClient.exchangeCodeForToken(code)
                .doOnNext(response -> {
                    String accessToken = response.getAccess_token();
                    tokenStorageService.saveAccessToken(accessToken);
                })
                .map(oAuthMappers::toOAuthCallBackClientResponse);
    }
}

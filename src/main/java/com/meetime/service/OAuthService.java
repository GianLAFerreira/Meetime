package com.meetime.service;

import com.meetime.controller.model.response.OAuthAutorizationResponse;
import com.meetime.controller.model.response.OAuthCallBackResponse;
import reactor.core.publisher.Mono;

public interface OAuthService {

    OAuthAutorizationResponse generateAuthorizationUrl();
    Mono<OAuthCallBackResponse> exchangeCodeForToken(String code);
}

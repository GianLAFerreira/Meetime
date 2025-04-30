package com.meetime.integration.oauth.client;

import com.meetime.integration.oauth.model.response.OAuthCallBackClientResponse;
import com.meetime.integration.oauth.model.response.OAuthGenerateAuthorizationResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface OAuthClient {

    OAuthGenerateAuthorizationResponse generateAuthorizationUrl();
    Mono<OAuthCallBackClientResponse> exchangeCodeForToken(String code);
}

package com.meetime.controller;

import com.meetime.controller.model.response.OAuthAutorizationResponse;
import com.meetime.controller.model.response.OAuthCallBackResponse;
import com.meetime.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/oauth/authorize")
    public OAuthAutorizationResponse getAuthorizationUrl() {

        return oAuthService.generateAuthorizationUrl();
    }


    @GetMapping("/oauth/callback")
    public Mono<OAuthCallBackResponse> handleCallback(@RequestParam("code") String code) {
        log.info("Callback sendo chamado {}", code);

        return oAuthService.exchangeCodeForToken(code);
    }
}
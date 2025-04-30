package com.meetime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .filter((request, next) -> next.exchange(request)
                        .retryWhen(
                                Retry.fixedDelay(3, Duration.ofSeconds(2))
                                        .filter(throwable -> throwable instanceof WebClientResponseException ex && ex.getStatusCode().value() == 429)
                        )
                )
                .build();
    }

}
package com.meetime.integration.oauth.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthCallBackClientResponse {

    private String token_type;
    private String refresh_token;
    private String access_token;
    private String expires_in;
}

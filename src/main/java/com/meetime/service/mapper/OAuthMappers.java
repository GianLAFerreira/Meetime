package com.meetime.service.mapper;

import com.meetime.controller.model.response.OAuthAutorizationResponse;
import com.meetime.controller.model.response.OAuthCallBackResponse;
import com.meetime.integration.oauth.model.response.OAuthCallBackClientResponse;
import com.meetime.integration.oauth.model.response.OAuthGenerateAuthorizationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OAuthMappers {

    OAuthAutorizationResponse toResponse(OAuthGenerateAuthorizationResponse clientResponse);

    OAuthCallBackResponse toOAuthCallBackClientResponse(OAuthCallBackClientResponse clientResponse);

}

package com.pretender.myApp.component;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

// deprecated
@Component
public class KakaoAccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
	
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest request) throws OAuth2AuthenticationException {
    	ClientRegistration clientRegistration = request.getClientRegistration();

    	RestClient restClient = RestClient.create();
    	
    	MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    	params.add("grant_type", "authorization_code");
    	params.add("client_id", clientRegistration.getClientId());
    	params.add("client_secret", clientRegistration.getClientSecret());
    	params.add("redirect_uri", request.getAuthorizationExchange().getAuthorizationRequest().getRedirectUri());
    	params.add("code", request.getAuthorizationExchange().getAuthorizationResponse().getCode());

    	ResponseEntity<Map> responseEntity = restClient.post()
    			.uri(clientRegistration.getProviderDetails().getTokenUri())
    			.contentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED)
    			.body(params)
        	    .retrieve()
        	    .toEntity(Map.class);

         Map<String, Object> body = responseEntity.getBody();

         String accessToken = (String) body.get("access_token");
         String tokenType = (String) body.get("token_type");
         Long expiresIn = ((Number) body.get("expires_in")).longValue();

         return OAuth2AccessTokenResponse.withToken(accessToken)
                 .tokenType(OAuth2AccessToken.TokenType.BEARER)
                 .expiresIn(expiresIn)
                 .build();
    }
}

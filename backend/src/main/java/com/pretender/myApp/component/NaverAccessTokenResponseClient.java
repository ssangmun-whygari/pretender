package com.pretender.myApp.component;

import java.awt.PageAttributes.MediaType;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

//deprecated
@Component
public class NaverAccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
    
    // reference : 
    // https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/oauth2/client/endpoint/DefaultAuthorizationCodeTokenResponseClient.html#getTokenResponse(org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest)
    // https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/oauth2/client/endpoint/RestClientAuthorizationCodeTokenResponseClient.html
    // https://github.com/spring-projects/spring-security/blob/8e2a4bf3562133c78230ec5a96ec993c5c92374b/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/endpoint/DefaultAuthorizationCodeTokenResponseClient.java#L58
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest request) {
        ClientRegistration clientRegistration = request.getClientRegistration();
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri(); // tokenUri : https://nid.naver.com/oauth2.0/token
        
        RestClient restClient = RestClient.create();
        tokenUri += "?response_type=code&";
        tokenUri += ("client_id=" + clientRegistration.getClientId() + "&");
        tokenUri += ("client_secret=" + clientRegistration.getClientSecret() + "&");
        tokenUri += ("code=" + request.getAuthorizationExchange().getAuthorizationResponse().getCode() + "&"); // OAuth2.0 표준 스펙
        tokenUri += ("state=" + request.getAuthorizationExchange().getAuthorizationRequest().getState()); // OAuth2.0 표준 스펙
        System.out.println("token uri : " + tokenUri);
                
        ResponseEntity<Map> responseEntity = restClient.get()
                .uri(tokenUri)
                .retrieve()
                .toEntity(Map.class);

        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseBody == null || !responseBody.containsKey("access_token")) {
            throw new OAuth2AuthenticationException("네이버 OAuth2 토큰 응답이 잘못되었습니다.");
        }
        
        // 응답 예시 : 
        // https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.4
		//        An example successful response:
		//
		//            HTTP/1.1 200 OK
		//            Content-Type: application/json;charset=UTF-8
		//            Cache-Control: no-store
		//            Pragma: no-cache
		//
		//            {
		//              "access_token":"2YotnFZFEjr1zCsicMWpAA",
		//              "token_type":"example",
		//              "expires_in":3600,
		//              "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
		//              "example_parameter":"example_value"
		//            }
        // https://developers.naver.com/docs/login/api/api.md#6-2-2--%EC%A0%91%EA%B7%BC-%ED%86%A0%ED%81%B0-%EB%B0%9C%EA%B8%89-%EC%9A%94%EC%B2%AD
        // {
		//		    "access_token":"AAAAQosjWDJieBiQZc3to9YQp6HDLvrmyKC+6+iZ3gq7qrkqf50ljZC+Lgoqrg",
		//		    "refresh_token":"c8ceMEJisO4Se7uGisHoX0f5JEii7JnipglQipkOn5Zp3tyP7dHQoP0zNKHUq2gY",
		//		    "token_type":"bearer",
		//		    "expires_in":"3600"
		// }
        
        // responseBody가 JSON 형식이기 때문에 OAuth2AccessTokenResponse 객체로 변환함
        // reference : https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/oauth2/core/endpoint/OAuth2AccessTokenResponse.html#withToken(java.lang.String)
        return OAuth2AccessTokenResponse.withToken(responseBody.get("access_token").toString())
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(Long.parseLong(responseBody.get("expires_in").toString()))
                .additionalParameters(responseBody)
                .build();
    }
}
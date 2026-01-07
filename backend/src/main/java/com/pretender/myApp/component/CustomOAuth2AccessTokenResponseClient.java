//package com.pretender.myApp.component;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
//import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
//import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
//import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CustomOAuth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
//
//	private final Map<String, OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>> clientMap;
//	
//	// 생성자
//	public CustomOAuth2AccessTokenResponseClient(List<OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>> clients) {
//	    this.clientMap = new HashMap<>();
//	    for (OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> client : clients) {
//	        if (client instanceof NaverAccessTokenResponseClient) {
//	            clientMap.put("naver", client);
//	        } else if (client instanceof KakaoAccessTokenResponseClient) {
//	            clientMap.put("kakao", client);
//	        }
//	    }
//	}
//	
//	@Override
//	public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest request) {
//	    String provider = request.getClientRegistration().getRegistrationId();
//	    return clientMap.getOrDefault(provider, new RestClientAuthorizationCodeTokenResponseClient())
//	            .getTokenResponse(request);
//	}
//}
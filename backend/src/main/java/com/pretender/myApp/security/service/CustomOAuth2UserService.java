package com.pretender.myApp.security.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.pretender.myApp.service.MembersService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
	
    @Autowired
    MembersService membersService;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
    	String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "naver" | "kakao" | "google" ...
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        if ("naver".equals(registrationId)) {
            log.info("attributes : " + attributes);
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            String providerId = String.valueOf(response.get("id"));
            String nickname = membersService.updateOAuthUser(registrationId, providerId);
            Map<String, Object> newAttributes = Map.of(
            		"providerId", providerId,
            		"nickname", nickname
            	);
            return new DefaultOAuth2User(List.of(() -> "ROLE_USER"), newAttributes, "providerId");
        } else if ("kakao".equals(registrationId)) {
        	log.info("attributes : " + attributes);
        	String providerId = String.valueOf(attributes.get("id"));
        	String nickname = membersService.updateOAuthUser(registrationId, providerId);
            Map<String, Object> newAttributes = Map.of(
            		"providerId", providerId,
            		"nickname", nickname
            	);
            return new DefaultOAuth2User(List.of(() -> "ROLE_USER"), newAttributes, "providerId");
        }
        throw new RuntimeException("지원되지 않는 소셜 로그인 registrationId입니다. registrationId의 예 : naver, kakao, google...");
    }
}

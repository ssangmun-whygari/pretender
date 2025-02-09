package com.pretender.myApp.util;

import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
    public static String getMemberId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            // 일반 로그인 (DB 기반 로그인)
            return authentication.getName();
        } else if (authentication instanceof OAuth2AuthenticationToken) {
        	OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        	System.out.println("oauthToken : " + oauthToken);
        	if (oauthToken.getAuthorizedClientRegistrationId() == "naver") {
        		// TODO
                Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
                // NOTE : 네이버는 정책상 소셜 로그인을 해도 회원의 네이버 이메일 주소를 알 수가 없음
                // https://developers.naver.com/forum/posts/29066
                // 대신 주어지는 token안의 고유값(예 : b7XrFZWhL7rPmBoIYmR6ARNgIIbRWf1BOcAoeJbYRlc)을 아이디처럼 활용할 예정
        	} else if (oauthToken.getAuthorizedClientRegistrationId() == "kakao") {
        		
        	}
            throw new RuntimeException("여기까지 실행됨");
        }
        return null;
    }
}

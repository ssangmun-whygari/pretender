package com.pretender.myApp.component;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {
	
	@Value("${frontEndBaseUrl}")
	private String frontEndBaseUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        System.err.println("OAuth2 로그인 실패: " + exception.getMessage()); // 실패 원인 출력
        response.sendRedirect(frontEndBaseUrl + "/socialLogin/fail");
    }
}
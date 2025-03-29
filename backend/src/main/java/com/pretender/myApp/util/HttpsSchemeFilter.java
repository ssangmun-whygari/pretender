package com.pretender.myApp.util;

import java.io.IOException;

import org.springframework.context.annotation.Profile;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

@Profile("dev-debug")
public class HttpsSchemeFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // TODO : X-Forwarded-Proto 헤더를 보내는 신뢰할 수 있는 프록시를 지정해야 함
        String forwardedProto = httpRequest.getHeader("X-Forwarded-Proto");

        if ("https".equalsIgnoreCase(forwardedProto)) {
        	System.out.println("프론트엔드로부터 X-Forwarded-Proto 헤더가 포함된 HTTPS 요청이 들어온다.");
        	
            // HTTPS 요청처럼 보이도록 래핑
            HttpServletRequestWrapper httpsRequest = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getScheme() {
                    return "https";
                }

                @Override
                public int getServerPort() {
                    return 443;
                }
            };
            chain.doFilter(httpsRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
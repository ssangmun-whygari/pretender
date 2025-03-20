package com.pretender.myApp.util;

import java.io.IOException;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Profile("dev-debug")
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("RequestHeadersLogger");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        StringBuilder sb = new StringBuilder("\n=== [REQUEST HEADERS] ===\n");

        Collections.list(request.getHeaderNames()).forEach(headerName -> 
            sb.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n")
        );

        log.info(sb.toString());

        filterChain.doFilter(request, response);
    }
}
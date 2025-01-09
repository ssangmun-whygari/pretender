package com.pretender.myApp.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.pretender.myApp.service.MyBatisUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	MyBatisUserDetailsService userDetailsService() {
		return new MyBatisUserDetailsService();
	}
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	// reference : https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/config/annotation/web/builders/HttpSecurity.html
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		// reference : https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html
		// TODO : 블로그에 적기...
		http.sessionManagement((session) -> {
			session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
		});
		http.cors(Customizer.withDefaults());
		http.httpBasic(Customizer.withDefaults());
		// CSRF 비활성화
		http.csrf(csrf -> csrf.disable());
		http.authorizeHttpRequests(authorizeHttpRequests -> {
			authorizeHttpRequests
				.requestMatchers(HttpMethod.OPTIONS).permitAll()
				.requestMatchers("/api/myPage/**").authenticated()
				.requestMatchers("/api/login").authenticated()
				.requestMatchers("/api/collection/**").authenticated()
				.requestMatchers("api/members/**").authenticated()
				.anyRequest().permitAll();
		});
		return http.build();
	}
	
	
	@Bean
	UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST","OPTIONS"));
		configuration.setAllowCredentials(true);
		configuration.addAllowedHeader("*");
	    configuration.addExposedHeader("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}

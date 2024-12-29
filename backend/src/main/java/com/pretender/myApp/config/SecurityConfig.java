package com.pretender.myApp.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.pretender.myApp.service.MyBatisUserDetailsService;

// reference : https://docs.spring.io/spring-security/reference/reactive/configuration/webflux.html
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	// 테스트용
//    @Bean
//    MapReactiveUserDetailsService userDetailsService() {
//    	PasswordEncoder encoder = passwordEncoder();
//    	
//        var user = User.withUsername("abc12345")
//                .password(encoder.encode("12345"))
//                .authorities("read")
//                .build();
//
//        return new MapReactiveUserDetailsService(user);
//    }
	
	@Bean
	MyBatisUserDetailsService userDetailsService() {
		return new MyBatisUserDetailsService();
	}
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
		http.httpBasic(Customizer.withDefaults());
		http.securityContextRepository(new WebSessionServerSecurityContextRepository());
		
		// CSRF 비활성화
		http.csrf(csrf -> csrf.disable());
		
		http.authorizeExchange((exchange) -> {
			exchange.pathMatchers(HttpMethod.OPTIONS).permitAll()
			.pathMatchers("/api/myPage/**").authenticated()
			.pathMatchers("/api/login").authenticated()
			.anyExchange().permitAll();
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

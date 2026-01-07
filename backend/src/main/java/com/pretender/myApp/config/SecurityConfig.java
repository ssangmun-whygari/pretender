package com.pretender.myApp.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.pretender.myApp.security.service.CustomOAuth2UserService;
import com.pretender.myApp.service.MyBatisUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Value("${frontEndBaseUrl}")
	private String frontEndBaseUrl;
	
	@Value("${whygari.naverClientId}")
	private String naverClientId;
	
	@Value("${whygari.naverClientPassword}")
	private String naverClientPassword;
	
	@Value("${whygari.kakaoClientId}")
	private String kakaoClientId;
	
	@Value("${whygari.kakaoClientSecret}")
	private String kakaoClientSecret;
	
	@Value("${googleClientId}")
	private String googleClientId;
	
	@Value("${googleClientSecret}")
	private String googleClientSecret;
	
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
	SecurityFilterChain configure(
			HttpSecurity http, 
			CorsConfigurationSource corsConfigurationSource,
			CustomOAuth2UserService customOAuth2UserService) throws Exception {
		
		// reference : https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html
		http.sessionManagement((session) -> {
			session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
		});
		http.cors(cors -> cors.configurationSource(corsConfigurationSource));
		http.httpBasic(Customizer.withDefaults());
		// CSRF 비활성화
		http.csrf(csrf -> csrf.disable());
		http.authorizeHttpRequests(authorizeHttpRequests -> {
			authorizeHttpRequests
				.requestMatchers(HttpMethod.OPTIONS).permitAll()
				.requestMatchers(HttpMethod.GET, "api/members/profile/image").permitAll()
				.requestMatchers(HttpMethod.GET, "/resource/image/**").permitAll()
				.requestMatchers("/images/public/**").permitAll()
				.requestMatchers("/api/myPage/**").authenticated()
				.requestMatchers("/api/collection/**").authenticated()
				.requestMatchers("/api/members/**").authenticated()
				.requestMatchers("/api/reviewLike").authenticated()
				.requestMatchers("/api/insertReview").authenticated()
				.requestMatchers("/api/login").permitAll()
				.requestMatchers("/api/login/**").permitAll()
				.requestMatchers("/api/popularMovies").permitAll()
				.anyRequest().permitAll();
		});
		
		// 소셜 로그인 관련 설정
		String defaultSuccessUrl = frontEndBaseUrl + "/socialLogin/success";
		String failureUrl = frontEndBaseUrl + "/socialLogin/fail";
		http.oauth2Login(oauth2 -> 
			oauth2
				.authorizationEndpoint(a -> a.baseUri("/api/login")) // 프론트에서 window.location.href = 'http://localhost:8080/api/login/naver' 이런식으로 사용함
				.defaultSuccessUrl(defaultSuccessUrl)
				.failureUrl(failureUrl)
				.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
		);
		
		return http.build();
	}

	@Bean
	UrlBasedCorsConfigurationSource corsConfigurationSource() {
		Logger logger = LoggerFactory.getLogger(getClass());
		logger.info("CORS에 등록된 Origin: {}", frontEndBaseUrl);
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(frontEndBaseUrl));
		configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT", "DELETE","OPTIONS"));
		configuration.setAllowCredentials(true);
		configuration.addAllowedHeader("*");
	    configuration.addExposedHeader("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	// reference : https://issuemaker99.tistory.com/172
	@Bean
	public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setSameSite("None"); // 'Strict', 'Lax', 'None' 중 선택
        serializer.setUseSecureCookie(true); // none 사용시 필수 설정
        return serializer;
	}
	
	// OAuth2.0 관련 configuration
	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(
				this.naverClientRegistration(),
				this.kakaoClientRegistration(),
				this.googleClientRegistration()
			);
	}
	
	// reference : https://developers.naver.com/docs/login/api/api.md
	private ClientRegistration naverClientRegistration() {
		return ClientRegistration.withRegistrationId("naver")
			.clientId(naverClientId)
			.clientSecret(naverClientPassword)
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.authorizationUri("https://nid.naver.com/oauth2.0/authorize")
			.redirectUri("{baseUrl}/login/oauth2/code/{registrationId}") // 예 : http://localhost:8080/login/oauth2/code/naver
			.tokenUri("https://nid.naver.com/oauth2.0/token")
			.userInfoUri("https://openapi.naver.com/v1/nid/me") // reference : https://developers.naver.com/apps/#/myapps/8MmQz34Hsx5yA9XT9SWu/playground
	        .userNameAttributeName("response")
			.clientName("Naver")
			.build();
	}
	
	private ClientRegistration kakaoClientRegistration() {
	return ClientRegistration.withRegistrationId("kakao")
            .clientId(kakaoClientId) 
            .clientSecret(kakaoClientSecret) 
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST) //카카오는 PostBody로 전송
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationUri("https://kauth.kakao.com/oauth/authorize")
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .tokenUri("https://kauth.kakao.com/oauth/token")
            .userInfoUri("https://kapi.kakao.com/v2/user/me")
            .userNameAttributeName("id") //카카오는 id가 유니크 값임
            .clientName("Kakao")
            .build();
	}
	
	private ClientRegistration googleClientRegistration() {		
		return ClientRegistration.withRegistrationId("google")
				.clientId(googleClientId)
				.clientSecret(googleClientSecret)
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
				.scope("profile","email")
				.authorizationUri("https://accounts.google.com/o/oauth2/auth")
				.tokenUri("https://oauth2.googleapis.com/token")
				.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
				.userNameAttributeName("sub") //google
				.clientName("Google")
				.build();
	}
	
}

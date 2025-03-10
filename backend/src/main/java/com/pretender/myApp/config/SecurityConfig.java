package com.pretender.myApp.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.pretender.myApp.component.CustomOAuth2FailureHandler;
import com.pretender.myApp.component.KakaoAccessTokenResponseClient;
import com.pretender.myApp.service.MyBatisUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private KakaoAccessTokenResponseClient kakaoAccessTokenResponseClient;
	
	@Value("${whygari.naverClientId}")
	private String naverClientId;
	
	@Value("${whygari.naverClientPassword}")
	private String naverClientPassword;
	
	@Value("${kakaoClientId}")
	private String kakaoClientId;
	
	@Value("${kakaoClientSecret}")
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
				.requestMatchers(HttpMethod.GET, "api/members/profile/image").permitAll()
				.requestMatchers(HttpMethod.GET, "/resource/image/**").permitAll()
				.requestMatchers("/images/public/**").permitAll()
				.requestMatchers("/api/myPage/**").authenticated()
				.requestMatchers("/api/login").authenticated()
				.requestMatchers("/api/collection/**").authenticated()
				.requestMatchers("/api/members/**").authenticated()
				.requestMatchers("/api/reviewLike").authenticated()
				.requestMatchers("/api/insertReview").authenticated()
				.requestMatchers("/api/login/**").permitAll()
				.anyRequest().permitAll();
		});
		http.oauth2Login(oauth2 -> oauth2
			    .authorizationEndpoint(authEndpoint -> authEndpoint.baseUri("/api/login"))
			    .tokenEndpoint(tokenEndpoint -> tokenEndpoint.accessTokenResponseClient(kakaoAccessTokenResponseClient))
			    .failureHandler(new CustomOAuth2FailureHandler())
			    .defaultSuccessUrl("http://localhost:3000/socialLogin/success", true)
			    .failureUrl("http://localhost:3000/socialLogin/fail")
			);
	
		return http.build();
	}
	
	
	@Bean
	UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT", "DELETE","OPTIONS"));
		configuration.setAllowCredentials(true);
		configuration.addAllowedHeader("*");
	    configuration.addExposedHeader("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	// OAuth2.0 관련 configuration
	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(
				this.naverClientRegistration()
				,this.kakaoClientRegistration()
				,this.googleClientRegistration()
			);
	}
	
	// reference : https://developers.naver.com/docs/login/api/api.md
	private ClientRegistration naverClientRegistration() {
		return ClientRegistration.withRegistrationId("naver")
			.clientId(naverClientId)
			.clientSecret(naverClientPassword)
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
			.scope() // reference : https://developers.worksmobile.com/kr/docs/auth-scope
			.authorizationUri("https://nid.naver.com/oauth2.0/authorize") // reference : https://developers.naver.com/docs/login/api/api.md#2--api-%EA%B8%B0%EB%B3%B8-%EC%A0%95%EB%B3%B4
			.tokenUri("https://nid.naver.com/oauth2.0/token")
			.userInfoUri("https://openapi.naver.com/v1/nid/me") // reference : https://developers.naver.com/apps/#/myapps/8MmQz34Hsx5yA9XT9SWu/playground
	        .userNameAttributeName("response") // ✅ 네이버의 JSON 응답 구조에 맞춰야 함
			.clientName("Naver")
			.build();
	}
	
	private ClientRegistration kakaoClientRegistration() {
		return ClientRegistration.withRegistrationId("kakao")
	            .clientId(kakaoClientId) 
	            .clientSecret(kakaoClientSecret) 
	            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)//카카오는 PostBody로 전송
	            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
	            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
	            .scope("profile_nickname", "account_email")
	            .authorizationUri("https://kauth.kakao.com/oauth/authorize")
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

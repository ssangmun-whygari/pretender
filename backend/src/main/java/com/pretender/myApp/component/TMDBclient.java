package com.pretender.myApp.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TMDBclient {
	@Value("${whygari.TMDBtoken}")
	private String token;
	
	@Autowired
	private RestClient.Builder builder;
	
	public RestClient getRestClient() {
		return builder
			.baseUrl("https://api.themoviedb.org")
			.defaultHeaders(httpHeaders -> {
				httpHeaders.add("Authorization", "Bearer " + token);
				httpHeaders.add("accept", "application/json");
			})
			.build();
	}
}

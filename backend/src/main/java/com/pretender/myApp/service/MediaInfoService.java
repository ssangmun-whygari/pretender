package com.pretender.myApp.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.pretender.myApp.component.TMDBclient;

import reactor.core.publisher.Mono;

@Service
public class MediaInfoService {
	@Autowired
	private TMDBclient client;
	
	public Mono<ResponseEntity<Map>> requestSearch(String query) {
		return client
			.getWebClient()
			.get()
			.uri(uriBuilder -> uriBuilder
				.path("/3/search/multi")
				.queryParam("query", query)
				.queryParam("language", "ko-KR")
				.queryParam("page", 1)
				.build()
				)
			.retrieve()
			.toEntity(Map.class);
	}
}

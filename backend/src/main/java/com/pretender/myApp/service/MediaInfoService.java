package com.pretender.myApp.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pretender.myApp.component.TMDBclient;

import reactor.core.publisher.Mono;

@Service
public class MediaInfoService {
	@Autowired
	private TMDBclient client;
	
	public Mono<ResponseEntity<Map>> requestSearch(String type, String query) {
		System.out.println("검색 요청 type : " + type + ", query : " + query);
		return client
			.getWebClient()
			.get()
			.uri(uriBuilder -> uriBuilder
				.path("/3/search/" + type)
				.queryParam("query", query)
				.queryParam("language", "ko-KR")
				.queryParam("include_adult", true)
				.queryParam("page", 1)
				.build()
				)
			.retrieve()
			.toEntity(Map.class);
	}

	public Mono<ResponseEntity<Map>> requestDetail(String type, String mediaId) {
		System.out.println("type : " + type);
		System.out.println("mediaId : " + mediaId);
		Mono<ResponseEntity<Map>> result = null;
		try {
			if (type.equals("tv")) { 
				result = 
					client.getWebClient()
					.get()
					.uri(uriBuilder -> uriBuilder
						.path("/3/tv/" + mediaId)
						.queryParam("language", "ko-KR")
						.build()
						)
					.retrieve()
					.toEntity(Map.class)
					.flatMap(responseEntity -> {
						Map<String, Object> body = responseEntity.getBody();
						Map<String, Object> filtered = new HashMap<>();
						filtered.put("name", body.get("name")); // tv 시리즈의 제목 (예 : 흑백요리사)
						filtered.put("networks", body.get("networks")); // contents privider (예 : 넷플릭스)
						filtered.put("overview", body.get("overview"));
						filtered.put("genres", body.get("genres"));
						return Mono.just(ResponseEntity.ok(filtered));
					});
				System.out.println(result);
				return result;
			} else if (type.equals("movie")) {
				// TODO
			}
		} catch (Exception e) {
			return Mono.just(ResponseEntity.badRequest().build());
		}
		return Mono.just(ResponseEntity.badRequest().build());
	}
}

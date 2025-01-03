package com.pretender.myApp.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pretender.myApp.component.TMDBclient;

@Service
public class MediaInfoService {
	@Autowired
	private TMDBclient client;
	
	public ResponseEntity<Map> requestSearch(String type, String query) {
		System.out.println("검색 요청 type : " + type + ", query : " + query);
		return client
			.getRestClient()
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

	public ResponseEntity<Map> requestDetail(String type, String mediaId) {
		System.out.println("type : " + type);
		System.out.println("mediaId : " + mediaId);
		ResponseEntity<Map> result = null;
		try {
			if (type.equals("tv")) { 
			Map<String, Object> filteredMap = 
				((Map<String, Object>) client.getRestClient()
					.get()
					.uri(uriBuilder -> uriBuilder
						.path("/3/tv/" + mediaId)
						.queryParam("language", "ko-KR")
						.build()
						)
					.retrieve()
					.toEntity(Map.class)
					.getBody()).entrySet().stream()
					.filter(entry -> {
						return Set.of("name", "networks", "overview", "genres", "poster_path").contains(entry.getKey());
					}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
				
				
				// return new ResponseEntity.ok(filteredMap);
			} else if (type.equals("movie")) {
				// TODO
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.badRequest().build();
	}
}

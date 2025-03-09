package com.pretender.myApp.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pretender.myApp.component.TMDBclient;
import com.pretender.myApp.model.CastLikeCategoryDTO;
import com.pretender.myApp.persistence.MediaInfoDAO;

@Service
public class MediaInfoService {
	@Autowired
	private TMDBclient client;
	@Autowired
	private MediaInfoDAO mediaInfoDAO;
	
	public ResponseEntity<Map> requestSearch(String type, String query, Integer page) {
		System.out.println("검색 요청 type : " + type + ", query : " + query + ", page : " + page);
		return client
			.getRestClient()
			.get()
			.uri(uriBuilder -> uriBuilder
				.path("/3/search/" + type)
				.queryParam("query", query)
				.queryParam("language", "ko-KR")
				.queryParam("include_adult", false)
				.queryParam("page", (page == null) ? 1 : page)
				.build()
				)
			.retrieve()
			.toEntity(Map.class);
	}

	public Map<String, Object> requestDetail(String type, String mediaId) throws Exception {
		System.out.println("type : " + type);
		System.out.println("mediaId : " + mediaId);
		Map<String, Object> result = null;
		if (type.equals("tv")) { 
		result = 
			((Map<String, Object>) client.getRestClient()
				.get()
				.uri(uriBuilder -> uriBuilder
					.path("/3/tv/" + mediaId)
					.queryParam("language", "ko-KR")
					.queryParam("append_to_response", "watch/providers")
					.build()
					)
				.retrieve()
				.toEntity(Map.class)
				.getBody()).entrySet().stream()
				.filter(entry -> {
					return Set.of("name", "title", "overview", "genres", "poster_path", "watch/providers").contains(entry.getKey());
				}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			return result;
		} else if (type.equals("movie")) {
		// TODO
		result = 
			((Map<String, Object>) client.getRestClient()
				.get()
				.uri(uriBuilder -> uriBuilder
					.path("/3/movie/" + mediaId)
					.queryParam("language", "ko-KR")
					.queryParam("append_to_response", "watch/providers")
					.build()
					)
				.retrieve()
				.toEntity(Map.class)
				.getBody()).entrySet().stream()
				.filter(entry -> {
					return Set.of("name", "title", "overview", "genres", "poster_path", "watch/providers").contains(entry.getKey());
				}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		return result;
		}
		return result;
	}

	public Map<String, Object> requestCast(String type, String mediaId) {
		System.out.println("type : " + type);
		System.out.println("mediaId : " + mediaId);
		Map<String, Object> result = null;
		if (type.equals("tv")) { 
//		result = 
//			((Map<String, Object>) client.getRestClient()
//				.get()
//				.uri(uriBuilder -> uriBuilder
//					.path("/3/tv/" + mediaId + "/credits")
//					.queryParam("language", "ko-KR")
//					.build()
//					)
//				.retrieve()
//				.toEntity(Map.class)
//				.getBody()).entrySet().stream()
//				.filter(entry -> {
//					return Set.of("cast").contains(entry.getKey());
//				}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//			return result;
			
			result = 
			((Map<String, Object>) client.getRestClient()
				.get()
				.uri(uriBuilder -> uriBuilder
					.path("/3/tv/" + mediaId + "/credits")
					.queryParam("language", "ko-KR")
					.build()
					)
				.retrieve()
				.toEntity(Map.class)
				.getBody());
			return result;
		} else if (type.equals("movie")) {
			result = 
			((Map<String, Object>) client.getRestClient()
				.get()
				.uri(uriBuilder -> uriBuilder
					.path("/3/movie/" + mediaId + "/credits")
					.queryParam("language", "ko-KR")
					.build()
					)
				.retrieve()
				.toEntity(Map.class)
				.getBody());
			return result;
		}
		return result;
	}

	public List<CastLikeCategoryDTO> getLikeCategory() {
		return mediaInfoDAO.getCastLikeCategory();
	}
	
	
}

package com.pretender.myApp.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pretender.myApp.component.TMDBclient;
import com.pretender.myApp.model.AISummaryDTO;
import com.pretender.myApp.model.CastLikeCategoryDTO;
import com.pretender.myApp.model.CastVotesDTO;
import com.pretender.myApp.model.VoteReasonsDTO;
import com.pretender.myApp.persistence.MediaInfoDAO;

@Service
public class MediaInfoService {
	@Autowired
	private TMDBclient client;
	@Autowired
	private MediaInfoDAO mediaInfoDAO;
	
	public ResponseEntity<Map> requestPopularMovies() {
//		throw new RuntimeException("일부러 에러 테트려보기");
		System.out.println("인기있는 영화 요청");
		return client
				.getRestClient()
				.get()
				.uri(uriBuilder -> uriBuilder
					.path("/3/movie/popular")
					.queryParam("language", "ko-KR")
					.queryParam("page", 1)
					.queryParam("region", "KR")
					.build()
					)
				.retrieve()
				.toEntity(Map.class);
	}
	
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
					return Set.of("name", "title", "overview", "genres", "poster_path", "backdrop_path", "watch/providers").contains(entry.getKey());
				})
				.filter(entry -> entry.getValue() != null)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		} else if (type.equals("movie")) {
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
					return Set.of("name", "title", "overview", "genres", "poster_path", "backdrop_path", "watch/providers").contains(entry.getKey());
				})
				.filter(entry -> entry.getValue() != null)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		// TODO : 평균 별점 DB에서 조회
		Float averageStars = mediaInfoDAO.getAverageStars(mediaId, type);
		System.out.println("평균별점 : " + averageStars);
		result.put("average_stars", averageStars);
		
		AISummaryDTO aiSummary = mediaInfoDAO.getAiSummary(mediaId, type);
		result.put("ai_summary", aiSummary);
		if (aiSummary != null) {
			Map<Integer, String> aiSummaryCategory = mediaInfoDAO.getAiSummaryCategory();
			result.put("ai_summary_category", aiSummaryCategory);
		}
		return result;
	}

	public Map<String, Object> requestCast(String type, String mediaId) {
		System.out.println("type : " + type);
		System.out.println("mediaId : " + mediaId);
		Map<String, Object> result = null;
		if (type.equals("tv")) { 			
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

	public List<CastVotesDTO> requestCastVotes(String mediaId, String type) {
		// 캐릭터 이름, 배우 이름, 투표수 가져오기
		return mediaInfoDAO.getCastVotesInfo(mediaId, type);
	}

	public List<VoteReasonsDTO> requestVoteReasons(String mediaId, String type, String characterId) {
		// 투표 이유와 투표 수 가져오기
		return mediaInfoDAO.getVotesResons(mediaId, type, characterId);
	}

	public List<String> requestAiSummaryProvidedList() {
		return mediaInfoDAO.getAiSummaryProvidedList();
	}

	public List<Map<String, String>> requesDetailFortAiSummaryProvidedList(List<String> idList) {
		return mediaInfoDAO.getDetailForAiSummaryProvidedList(idList);
	}
}

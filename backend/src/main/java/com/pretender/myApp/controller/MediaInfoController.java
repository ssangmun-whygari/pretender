package com.pretender.myApp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pretender.myApp.model.CastLikeCategoryDTO;
import com.pretender.myApp.model.CastVotesDTO;
import com.pretender.myApp.model.VoteReasonsDTO;
import com.pretender.myApp.model.VoteRequestDTO;
import com.pretender.myApp.service.MediaInfoService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MediaInfoController {
	@Autowired
	private MediaInfoService mediaInfoService;
	
	@GetMapping("/api/popularMovies")
	public ResponseEntity<Map> getPopularMovies() {
		ResponseEntity<Map> result = null;
		try {
			result = mediaInfoService.requestPopularMovies();
		} catch (Exception e) {
			log.error("TMDB API 호출 중 문제 생김", e);
			return ResponseEntity.internalServerError().body(null);
		}
		return ResponseEntity.ok(result.getBody()); // 헤더는 복사 안함
	}
	
	@GetMapping("/api/search")
	public ResponseEntity<Map> searchMediaByWord(
			@RequestParam String type,
			@RequestParam String query,
			@RequestParam(required = false) Integer page) {
		ResponseEntity<Map> result = mediaInfoService.requestSearch(type, query, page);
		return ResponseEntity.ok(result.getBody()); // 헤더는 복사 안함
	}
	
	@GetMapping("/api/detail")
	public ResponseEntity<Map<String, Object>> getDetailById(
			@RequestParam String type,
			@RequestParam String mediaId) {
		ResponseEntity<Map<String, Object>> result = mediaInfoService.requestDetail(type, mediaId);
		return result;
	}
	
	@GetMapping("/api/aiSummaryProvided")
	public ResponseEntity<List<String>> getAiSummaryProvidedList() {
		List<String> result = null;
		try {
			result = mediaInfoService.requestAiSummaryProvidedList();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/api/detail/aiSummaryProvided")
	public ResponseEntity<List<Map<String, String>>> getAiSummaryProvidedList(
			@RequestBody List<String> idList
			) {
		System.out.println("도착한 idList : " + idList.toString());
		List<Map<String, String>> result = null;
		try {
			result = mediaInfoService.requesDetailFortAiSummaryProvidedList(idList);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(result);
	}
	
	// 어떤 작품의 배역 정보를 반환
	@GetMapping("/api/detail/cast")
	public ResponseEntity<Map> getCastById(
			@RequestParam String type,
			@RequestParam String mediaId) {
		Map<String, Object> result = null;
		try {
			result = mediaInfoService.requestCast(type, mediaId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(result);
	}
	
	// 가능한 투표 이유의 종류를 반환
	@GetMapping("/api/detail/cast/likeCategory")
	public ResponseEntity<List<CastLikeCategoryDTO>> getLikeCategory() {
		List<CastLikeCategoryDTO> result = null;
		try {
			result = mediaInfoService.getLikeCategory();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(result);
	}
	
	// 한 작품에 대하여 캐릭터 당 얻은 투표 수를 반환, 투표 수가 큰 순서대로 정렬됨
	@GetMapping("api/detail/castVotes")
	public ResponseEntity<List<CastVotesDTO>> getCastVotes(@RequestParam String mediaId, @RequestParam String type) {
		List<CastVotesDTO> castVotes = mediaInfoService.requestCastVotes(mediaId,type);
		return ResponseEntity.ok(castVotes);
	}
	
	// 어떤 회원이 한 작품에 투표했는 지 반환
	@GetMapping("api/detail/vote")
	public ResponseEntity<Map<String, Object>> getVote(
		@RequestParam String memberId,
		@RequestParam String mediaId,
		@RequestParam String type) {
		boolean exist;
		Map<String, Object> map = new HashMap<>();
		try {
			exist = mediaInfoService.getVote(memberId, mediaId, type);
		} catch (Exception e) {
			exist = false;
			e.printStackTrace();
		}
		map.put("result", exist);
		return ResponseEntity.ok(map);
	}
	
	
	@PostMapping("api/detail/vote")
		public ResponseEntity<Map<String, Object>> vote(@RequestBody VoteRequestDTO req) {
			String memberId = req.getMemberId();
			String mediaId = req.getMediaId();
			String characterId = req.getCharacterId();
			String type = req.getType();
			int why = req.getWhy();
			Map<String, Object> map = new HashMap<>();
			int result = mediaInfoService.vote(memberId, mediaId, characterId, type, why);
			if (result == 0) {
				map.put("result", "duplicated");
			} else if (result == 1) {
				map.put("result", "success");
			} else {
				map.put("result", "fail");
			}
			return ResponseEntity.ok(map);
		}
			
	// 어떤 작품의 캐릭터에 대해 투표 이유를 집계하여 반환
	@GetMapping("api/detail/votesReasons")
	public ResponseEntity<List<VoteReasonsDTO>> getVotesReasons(@RequestParam String mediaId, 
			@RequestParam String type, @RequestParam String characterId) {
		List<VoteReasonsDTO> reasons = mediaInfoService.requestVoteReasons(mediaId, type, characterId);
		return ResponseEntity.ok(reasons);
	}
}
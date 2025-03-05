package com.pretender.myApp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pretender.myApp.model.CastLikeCategoryDTO;
import com.pretender.myApp.model.CastVotesDTO;
import com.pretender.myApp.model.VoteReasonsDTO;
import com.pretender.myApp.service.MediaInfoService;

@RestController
public class MediaInfoController {
	@Autowired
	private MediaInfoService mediaInfoService;
	
	@GetMapping("/api/search")
	public ResponseEntity<Map> searchMediaByWord(
			@RequestParam String type,
			@RequestParam String query) {
		ResponseEntity<Map> result = mediaInfoService.requestSearch(type, query);
		return result;
	}
	
	@GetMapping("/api/detail")
	public ResponseEntity<Map> getDetailById(
			@RequestParam String type,
			@RequestParam String mediaId) {
		Map<String, Object> result = null;
		try {
			result = mediaInfoService.requestDetail(type, mediaId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(result);
	}
	
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
	
	@GetMapping("api/detail/castVotes")
	public ResponseEntity<List<CastVotesDTO>> getCastVotes(@RequestParam String mediaId, @RequestParam String type) {
		System.out.println("it's working!"+mediaId+" : "+type);
		List<CastVotesDTO> castVotes = mediaInfoService.requestCastVotes(mediaId,type);
		return ResponseEntity.ok(castVotes);
	}
	
	@GetMapping("api/detail/votesReasons")
	public ResponseEntity<List<VoteReasonsDTO>> getVotesReasons(@RequestParam String mediaId, 
			@RequestParam String type, @RequestParam String characterId) {
		List<VoteReasonsDTO> reasons = mediaInfoService.requestVoteReasons(mediaId, type, characterId);
		return ResponseEntity.ok(reasons);
	}
}
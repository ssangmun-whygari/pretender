package com.pretender.myApp.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pretender.myApp.model.CollectionItemDTO;
import com.pretender.myApp.service.MediaCollectionService;
import com.pretender.myApp.util.SecurityUtil;

@RestController
public class MediaCollectionController {
	@Autowired
	MediaCollectionService mediaCollectionService;
	
	@GetMapping("/api/collection/watchList")
	public ResponseEntity<Object> getWatchList(
			Authentication token,
			@RequestParam(required = false) String mediaId,
			@RequestParam(required = false) String mediaType
		) {
		if (token == null) {
			return ResponseEntity.badRequest().body((Object) "잘못된 요청입니다. 로그인 후 다시 시도해주세요.");
		}
		// reference: https://docs.spring.io/spring-security/site/docs/4.0.x/apidocs/org/springframework/security/authentication/UsernamePasswordAuthenticationToken.html
		System.out.println("GET /api/collection/watchList");
		System.out.println("token : " + token);
		System.out.println("token.getName() : " + token.getName());
		String memberId = token.getName();
		if (mediaId == null || mediaType == null) {
			Map<String, Object> map = new HashMap<>();
			List<CollectionItemDTO> watchList = mediaCollectionService.getWatchList(memberId);
			return ResponseEntity.ok(watchList);
		} else {
			// id와 type으로 watchList를 조회하여 작품이 포함되어있는 지 반환한다.
			return ResponseEntity.ok(mediaCollectionService.includesInWatchList(memberId, mediaId, mediaType));
		}
	}
	
	@PostMapping("/api/collection/watchList")
	public ResponseEntity<String> addItemInWatchList(
			Authentication token,
			@RequestParam String mediaId,
			@RequestParam String mediaType,
			@RequestParam String mediaTitle
		) {
		System.out.println("POST /api/collection/watchList");
//		if (token == null) {
//			// reference : https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpStatus.html
//			return Mono.just(ResponseEntity.status(HttpStatus.FOUND).body("로그인 페이지로 이동합니다."));
//		}
		String memberId = token.getName();
		if (mediaCollectionService.addItemInWatchList(memberId, mediaId, mediaType) == true) {
			return ResponseEntity.ok("내가 본 리스트에 추가되었습니다.");
		} else {
			return ResponseEntity.internalServerError().body("서버에 문제가 생겼습니다. 잠시 후 다시 시도해주세요.");
		}
	}
	
	@GetMapping("/api/collection/userCollection")
	public ResponseEntity<String> getUserCollection(Principal user) {
		if (user == null) {
			return ResponseEntity.badRequest().body("잘못된 요청입니다. 로그인 후 다시 시도해주세요.");
		}
		System.out.println("요청한 user : " + user);
		return ResponseEntity.ok("ok");
	}
}

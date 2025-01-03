package com.pretender.myApp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
		ResponseEntity<Map> result = mediaInfoService.requestDetail(type, mediaId);
		return result;
	}
}
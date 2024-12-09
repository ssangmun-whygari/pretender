package com.pretender.myApp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.pretender.myApp.service.MediaInfoService;

import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
public class MediaInfoController {
	@Autowired
	private MediaInfoService mediaInfoService;
	
	@GetMapping("/search")
	public Mono<ResponseEntity<Map>> index(
			@RequestParam String query) {
		Mono<ResponseEntity<Map>> result = mediaInfoService.requestSearch(query);
		return result;
	}
}
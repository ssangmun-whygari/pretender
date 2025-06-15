package com.pretender.myApp.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {
	
	@Value("${resourceS3bucketUrl}")
	private String resourceS3bucketUrl;
	
	// TODO : 계절에 따라 다른 이미지를 보여줘야 함
	@GetMapping("/resource/backgroundImage")
	public ResponseEntity<Object> getBackgroundImage() {
		String s3Url = resourceS3bucketUrl + "/images/public/겨울.png";
		return ResponseEntity.status(HttpStatus.FOUND) // 302 Redirect
                .location(URI.create(s3Url))
                .build();
	}
	
	@GetMapping("/resource/image")
	public ResponseEntity<Object> getBackgroundImage( // 함수 오버로딩
			@RequestParam String filename
			) {
		
		String s3Url = resourceS3bucketUrl + "/images/public/" + filename;
		return ResponseEntity.status(HttpStatus.FOUND) // 302 Redirect
                .location(URI.create(s3Url))
                .build();
	}
	
	@GetMapping("/resource/characterVoteNoImage")
	public ResponseEntity<Object> getImage(
			) {
		
		String s3Url = resourceS3bucketUrl + "/images/public/characterVoteNoImage.webp";
		return ResponseEntity.status(HttpStatus.FOUND) // 302 Redirect
                .location(URI.create(s3Url))
                .build();
	}
}

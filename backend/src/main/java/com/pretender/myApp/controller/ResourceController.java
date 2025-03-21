package com.pretender.myApp.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {
	// TODO : 계절에 따라 다른 이미지를 보여줘야 함
	@GetMapping("/resource/backgroundImage")
	public ResponseEntity<Object> getBackgroundImage() {
		
		// File imageFile = new File("src/main/resources/static/images/public/겨울.png");
		Resource resource;
		try {
			// resource = new UrlResource(Paths.get(imageFile.getAbsolutePath()).normalize().toUri());
			resource = new ClassPathResource("static/images/public/겨울.png");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("배경 이미지 파일을 찾지 못했습니다.");
		}
		return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "image/*")
            .body(resource);
	}
	
	@GetMapping("/resource/image")
	public ResponseEntity<Object> getBackgroundImage(
			@RequestParam String filename
			) {
		// File imageFile = new File("src/main/resources/static/images/public/" + filename);
		Resource resource;
		try {
			// resource = new UrlResource(Paths.get(imageFile.getAbsolutePath()).normalize().toUri());
			resource = new ClassPathResource("static/images/public/" + filename);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("배경 이미지 파일을 찾지 못했습니다.");
		}
		return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "image/*")
            .body(resource);
	}
}

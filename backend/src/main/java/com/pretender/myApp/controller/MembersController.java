package com.pretender.myApp.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pretender.myApp.model.MembersDTO;
import com.pretender.myApp.service.MembersService;


@RestController
public class MembersController {
	@Autowired
  	private MembersService MembersService;
  
	@PostMapping("/api/signup")
	public ResponseEntity<String> registerUser(@RequestBody MembersDTO signUpRequest) {
	    // String result = MembersService.registerUser(signUpRequest);
		String result = null;
	    if (result.equals("회원가입 성공!")) {
	        return ResponseEntity.ok(result);
	    } else {
	        return ResponseEntity.badRequest().body(result);
	    }
	}
	
	@GetMapping("/api/login")
	public ResponseEntity<Map<String, Object>> login() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "로그인 성공");
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/api/authenticated")
	public Principal user(Principal user) {
		System.out.println("user : " + user);
	    return user;
	}
	
	@GetMapping("/api/myPage")
	public ResponseEntity<Map<String, Object>> myPage() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "어서 오세요");
		return ResponseEntity.ok(response);
	}
}
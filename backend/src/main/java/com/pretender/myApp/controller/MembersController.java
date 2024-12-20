package com.pretender.myApp.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import com.pretender.myApp.vodto.UserDTO;
import com.pretender.myApp.service.MembersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
public class MembersController {
  @Autowired
  private MembersService MembersService;
  
  @PostMapping("/api/signup")
  public ResponseEntity<String> registerUser(@RequestBody UserDTO signUpRequest) {
      String result = MembersService.registerUser(signUpRequest);
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
		return ResponseEntity.status(HttpStatus.OK).body(response);
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
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
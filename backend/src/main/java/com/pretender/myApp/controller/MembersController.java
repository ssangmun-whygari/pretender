package com.pretender.myApp.controller;


import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import com.pretender.myApp.model.MembersDTO;
import com.pretender.myApp.service.MembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    int result = 0;
    try {
      result = MembersService.registerUser(signUpRequest);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }

    if (result != 0) {
      return ResponseEntity.ok("회원가입이 완료되었습니다.");
    } else {
      return ResponseEntity.badRequest().body("회원가입에 실패하였습니다.");
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
		System.out.println("SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());
		System.out.println("user : " + user);
	    return user;
	}
	
	@GetMapping("/api/myPage")
	public ResponseEntity<Map<String, Object>> myPage() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "어서 오세요");
		return ResponseEntity.ok(response);
		// TODO : 로그인 되어 있지 않을 시 잘못된 요청 페이지 보여줘야 함
	}

}
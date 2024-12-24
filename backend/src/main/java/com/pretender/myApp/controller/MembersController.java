package com.pretender.myApp.controller;


import com.pretender.myApp.vodto.MembersDTO;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


import com.pretender.myApp.service.MembersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/api/signup")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")//CORS
public class MembersController {

    @Autowired
    private MembersService MembersService;

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody MembersDTO signUpRequest) {
        
    	int result = 0;
		try {
			result = MembersService.registerUser(signUpRequest);
			
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
        
        if (result != 0) {
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
    
    	}else {
            return ResponseEntity.badRequest().body("회원가입에 실패하였습니다.");
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
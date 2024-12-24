package com.pretender.myApp.controller;

import com.pretender.myApp.service.MembersService;
import com.pretender.myApp.vodto.MembersDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
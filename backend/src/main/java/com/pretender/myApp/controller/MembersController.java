package com.pretender.myApp.controller;

import com.pretender.myApp.vodto.UserDTO;
import com.pretender.myApp.service.MembersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/signup")
public class MembersController {

    @Autowired
    private MembersService MembersService;

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody UserDTO signUpRequest) {
        String result = MembersService.registerUser(signUpRequest);
        if (result.equals("회원가입 성공!")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
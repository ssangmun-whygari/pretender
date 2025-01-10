package com.pretender.myApp.controller;


import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pretender.myApp.model.MembersDTO;
import com.pretender.myApp.service.MembersService;


@RestController
public class MembersController {
  @Autowired
  private MembersService membersService;

  @PostMapping("/api/signup")
  public ResponseEntity<String> registerUser(@RequestBody MembersDTO signUpRequest) {

    int result = 0;
    try {
      result = membersService.registerUser(signUpRequest);
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
	
	@PostMapping("api/members/profile/image")
	public ResponseEntity<String> postProfileImage (
			UsernamePasswordAuthenticationToken token,
			@RequestParam("profileImage") MultipartFile file
		) {
		System.out.println("POST api/members/profile/image");
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("파일이 업로드되지 않았습니다.");
		}
		
        String mimeType = file.getContentType();
        if (mimeType == null || !mimeType.startsWith("image/")) {
            return ResponseEntity.badRequest().body("이미지 파일만 업로드 가능합니다.");
        }
        
        try {
        	// TODO : 이미지 작은 크기로 변환하여 저장
        	membersService.updateProfileImage(token.getName(), file);
        } catch (Exception e) {
        	e.printStackTrace();
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 중 오류가 발생했습니다.");
        }
        return ResponseEntity.ok("이미지 등록이 완료되었습니다.");
	}
	
	@GetMapping("api/members/profile/image")
	public ResponseEntity<Object> getProfileImage (
			UsernamePasswordAuthenticationToken token
		) {
		System.out.println("GET api/members/profile/image");
		try {
			Resource resource = membersService.getProfileImage(token.getName());
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "image/*")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 조회 중 오류가 발생했습니다.");
		}
	}
}
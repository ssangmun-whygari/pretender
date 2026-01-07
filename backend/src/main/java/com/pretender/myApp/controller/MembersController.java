package com.pretender.myApp.controller;


import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.web.exchanges.HttpExchange.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pretender.myApp.exception.SignUpException;
import com.pretender.myApp.model.MembersDTO;
import com.pretender.myApp.model.MyActivitiesDTO;
import com.pretender.myApp.security.model.PretenderUserDetails;
import com.pretender.myApp.service.MembersService;


@RestController
public class MembersController {
  @Autowired
  private MembersService membersService;
  private static final int PAGE_SIZE_MyActivities = 10;
  

  @PostMapping("/api/signup")
  public ResponseEntity<Map<String, Object>> registerUser(@RequestBody MembersDTO signUpRequest) {

    int result = 0;
    try {
    	result = membersService.registerUser(signUpRequest);
    } catch (SignUpException e) {
    	Map<String, Object> responseBody = e.getBody();
    	responseBody.put("result", "fail");
    	return ResponseEntity.badRequest().body(responseBody);
    }

    if (result != 0) {
    	Map<String, Object> responseBody = Map.of("result", "success");
    	return ResponseEntity.ok(responseBody);
	} else {
		Map<String, Object> responseBody = Map.of("result", "fail", "errorCode", "unknown", "message", "회원가입에 실패하였습니다");
	    return ResponseEntity.badRequest().body(responseBody);
	}
  }

	@GetMapping("/api/login")
	public ResponseEntity<Map<String, Object>> login() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "로그인 성공");
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/api/authenticated")
	public Map<String, Object> user(Authentication auth) {
		Map<String, Object> res = new LinkedHashMap<>();
		
		// 로그인되어 있지 않다면
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            res.put("authenticated", false);
            return res;
        }
        res.put("authenticated", true);
        res.put("authorities", auth.getAuthorities().toString());
        
        Object principal = auth.getPrincipal();
        // 소셜 로그인(OAuth2)
        if (principal instanceof OAuth2User oAuth2User) {
        	Map<String, Object> attributes = oAuth2User.getAttributes();
        	String providerId = String.valueOf(attributes.get("providerId"));
        	String nickname = String.valueOf(attributes.get("nickname"));
            res.put("loginType", "OAUTH2");
            res.put("providerId", providerId);
            res.put("nickname", nickname);
            return res;
        }
        // 로컬(DB) 로그인(UserDetails 기반)
        if (principal instanceof PretenderUserDetails userDetails) {
            res.put("loginType", "LOCAL");
            res.put("nickname", userDetails.getNickname());
            return res;
        }
        
        res.put("loginType", "UNKNOWN");
        res.put("principal", String.valueOf(principal));
        return res;
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
			Authentication token,
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
        
    	long MAX_FILE_SIZE = 1 * 1024 * 1024; // 1MB

    	if (file.getSize() > MAX_FILE_SIZE) {
    	    return ResponseEntity.badRequest().body("파일 크기는 1MB를 초과할 수 없습니다.");
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
	
	/* deprecated
	@GetMapping("api/members/profile/image")
	public ResponseEntity<Object> getProfileImage (
			@RequestParam(required = false) String memberId,
			Authentication token
		) {
		System.out.println("GET api/members/profile/image");
		try {
			Resource resource;
			if (memberId == null) {
				resource = membersService.getProfileImage(token.getName()); // 로그인한 아이디로 조회
			} else {
				resource = membersService.getProfileImage(memberId); // 멤버 아이디로 조회
			}
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
	} */ 
	
	@GetMapping("api/members/profile/image")
	public ResponseEntity<Object> getProfileImage (
			@RequestParam(required = false) String memberId,
			Authentication token
		) {
		System.out.println("GET api/members/profile/image");
		String s3Url;
		try {
			if (memberId == null) {
				s3Url = membersService.getProfileImagePath(token.getName()); // 로그인한 아이디로 조회
			} else {
				s3Url = membersService.getProfileImagePath(memberId); // 멤버 아이디로 조회
			}
			return ResponseEntity.status(HttpStatus.FOUND) // 302 Redirect
		            .location(URI.create(s3Url))
		            .build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 조회 중 오류가 발생했습니다.");
		}
	}
	
	@GetMapping("api/members/nickname") 
	public ResponseEntity<Object> getNickname (
			Authentication token
		){
		System.out.println("GET api/members/nickname");
		Map<String, Object> response = new HashMap<>();
		String nickname = membersService.getNickname(token.getName());
		if (nickname != null) {
			response.put("nickname", nickname);
			return ResponseEntity.ok(nickname);
		} else {
			return ResponseEntity.badRequest().body("닉네임이 존재하지 않습니다.");
		}
	}
	
	
	@GetMapping("api/members/myActivites")
	public ResponseEntity<Object> getMyActivities (@RequestParam(defaultValue = "0") int page){
		Authentication auth =SecurityContextHolder.getContext().getAuthentication();	
		 if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			  return ResponseEntity.badRequest().body("로그인이 필요합니다.");
	        }
		 
		String userId = auth.getName();
		
		int size = PAGE_SIZE_MyActivities;
		int totalActCount = membersService.getTotalActivities(userId);
		int totalPages = (int)Math.ceil((double)totalActCount/size);
		List<MyActivitiesDTO> myActivities = membersService.selectMyActivitiesInfo(userId,page,size);
		
		Map<String, Object> response = new HashMap<>();
		response.put("totalPages", totalPages);
		response.put("page", page);
		response.put("size", size);
		response.put("myActivities", myActivities);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("api/members/searchMyAct")
	public ResponseEntity<Object> searchMyActivities (@RequestParam(defaultValue = "0") int page, String word){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return ResponseEntity.badRequest().body("로그인이 필요합니다.");
		}
		String userId = auth.getName();
		
		int size = PAGE_SIZE_MyActivities;
		int totalSearchCount = membersService.getTotalSearchAct(userId,word);
		int totalPages =(int)Math.ceil((double)totalSearchCount/size);
		List<MyActivitiesDTO> searchMyActResults = membersService.searchMyAct(userId,word,page,size);
		Map<String, Object> response = new HashMap<>();
		response.put("totalPages", totalPages);
		response.put("page", page);
		response.put("size", size);
		response.put("searchMyActResults", searchMyActResults);
		return ResponseEntity.ok(response);
	}
	
	
}
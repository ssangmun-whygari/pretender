package com.pretender.myApp.controller;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pretender.myApp.service.SampleService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class SampleController {
	@Autowired
	private SampleService sampleService;
	
	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}
	
	@GetMapping("/testDB")
	public String testDB() {
		List<String> names = sampleService.getNames();
		String result = "";
		for (String name : names) {
			result += name;
			result += ",";
		}
		return result;
	}
	
	// 스프링 서버가 받은 요청의 헤더를 출력
	@GetMapping("/debug/headers")
	public Map<String, Object> headers(HttpServletRequest req) {
	    Map<String, Object> map = new LinkedHashMap<>();
	    Enumeration<String> names = req.getHeaderNames();
	    while (names.hasMoreElements()) {
	        String name = names.nextElement();
	        map.put(name, Collections.list(req.getHeaders(name)));
	    }
	    return map;
	}
}
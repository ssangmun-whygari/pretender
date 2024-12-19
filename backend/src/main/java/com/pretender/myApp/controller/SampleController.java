package com.pretender.myApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pretender.myApp.service.SampleService;

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
	
}
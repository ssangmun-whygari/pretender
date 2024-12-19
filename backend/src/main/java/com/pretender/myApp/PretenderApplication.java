package com.pretender.myApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication
public class PretenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(PretenderApplication.class, args);
	}

}
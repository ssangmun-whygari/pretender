package com.pretender.myApp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pretender.myApp.model.PretenderUserDetails;
import com.pretender.myApp.persistence.MembersDAO;

import reactor.core.publisher.Mono;

public class MyBatisUserDetailsService implements ReactiveUserDetailsService {

	@Autowired
	private MembersDAO membersDAO;
	
	
	@Override
	public Mono<UserDetails> findByUsername(String username) {
		PretenderUserDetails user = membersDAO.findUserByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(username + "을 가진 회원이 발견되지 않았습니다.");
		}
		user.encodePassword(); // BCrypt
		
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		if (user.getIsAdmin() == false) { // 일반회원
			grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
		} else { // 관리자
			grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
		}
		user.setAuthorities(grantedAuthorities);
		
		System.out.println("user : " +  user.toString());
		System.out.println("user.password : " + user.getPassword());
		return Mono.just(user); 
	}

}

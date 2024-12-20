package com.pretender.myApp.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.ToString;

@ToString
public class PretenderUserDetails implements UserDetails {

	private String username;
	private String password;
	private boolean enabled;
	private List<GrantedAuthority> authorities;
	private boolean isAdmin;
	private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	public PretenderUserDetails(String username, String password, boolean eanabled, boolean isAdmin) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.isAdmin = isAdmin;
	}
	
	public void encodePassword() {
	    this.password = passwordEncoder.encode(this.password);
	}
	
	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}
	
	public boolean getIsAdmin() {
		return this.isAdmin;
	}
	
	public void setIsAdmin(boolean bool) {
		this.isAdmin = bool;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}
	
}

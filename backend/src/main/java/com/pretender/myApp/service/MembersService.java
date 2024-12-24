package com.pretender.myApp.service;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pretender.myApp.persistence.MembersDAO;
import com.pretender.myApp.vodto.MembersDTO;

@Service
public class MembersService {
		
		@Autowired
		private MembersDAO membersDAO;
	
		private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

	    //비밀번호 검증 패턴 (6~15자, 대문자, 소문자, 숫자, 특수문자 포함)
	    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{6,15}$";


	    public int registerUser(MembersDTO membersDTO) {
	    	
	        if (!isValidEmail(membersDTO.getId())) {
	            throw new IllegalArgumentException("아이디는 이메일 형식이어야 합니다.");
	        }

	        if (!isValidPassword(membersDTO.getPassword())) {
	            throw new IllegalArgumentException("비밀번호는 6~15자, 대문자, 소문자, 숫자, 특수문자 포함이어야 합니다.");
	        }
	        
	        if(isDuplicateId(membersDTO.getId())){
	        	throw new IllegalArgumentException("이미 가입한 회원입니다.");
	        }
	        
	        if(isDuplicateNickname(membersDTO.getNickname())) {
	        	throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
	        }

	        membersDTO.setStatus("active");
	        membersDTO.setIsAdmin("0");

	        return membersDAO.insertUser(membersDTO);
	    }
	    

	    private boolean isValidEmail(String email) {
	        return Pattern.matches(EMAIL_REGEX, email);
	    }

	    private boolean isValidPassword(String password) {
	        return Pattern.matches(PASSWORD_REGEX, password);
	    }
	    
	    private boolean isDuplicateId(String email) {
	    	return membersDAO.countById(email) > 0;
	    }
	    
	    private boolean isDuplicateNickname(String nickname) {
	    	return membersDAO.countByNickname(nickname) > 0;
	    }

}

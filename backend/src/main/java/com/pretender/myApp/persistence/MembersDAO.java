package com.pretender.myApp.persistence;


import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pretender.myApp.vodto.MembersDTO;



@Repository
public class MembersDAO {
	
	private static final String ns = "com.pretender.myApp.mapper.membersMapper.";
	
	@Autowired
	 private SqlSession ses;
	
	 // 이메일 중복 체크
	 public int countById(String id) {
		 return ses.selectOne(ns +"countById", id);
	 }
	 
	 public int countByNickname(String nickname) {
		 return ses.selectOne(ns +"countByNickname", nickname);
	 }

	 // 회원 정보 저장
	 public int insertUser(MembersDTO membersDTO) {
		 return ses.insert(ns +"insertUser", membersDTO);
	 }
}

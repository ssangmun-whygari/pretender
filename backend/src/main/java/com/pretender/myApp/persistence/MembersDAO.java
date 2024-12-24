package com.pretender.myApp.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pretender.myApp.model.MembersDTO;
import com.pretender.myApp.model.PretenderUserDetails;

@Component
public class MembersDAO {
	@Autowired
	private SqlSession ses;
	private String ns = "com.pretender.myApp.mapper.membersMapper.";
	
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
	public List<String> selectNames() {
		return ses.selectList(ns + "selectName");
	}

	public PretenderUserDetails findUserByUsername(String username) {
		return ses.selectOne(ns + "findUserByUsername", username);
	}

}

package com.pretender.myApp.persistence;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pretender.myApp.model.PretenderUserDetails;

@Component
public class MembersDAO {
	@Autowired
	private SqlSession ses;
	private String ns = "com.pretender.myApp.mapper.membersMapper.";
	
	public List<String> selectNames() {
		return ses.selectList(ns + "selectName");
	}

	public PretenderUserDetails findUserByUsername(String username) {
		return ses.selectOne(ns + "findUserByUsername", username);
	}
}

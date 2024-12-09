package com.pretender.myApp.persistence;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SampleDAO {
	@Autowired
	private SqlSession ses;
	private String ns = "com.pretender.myApp.mapper.sampleMapper.";
	
	public List<String> selectNames() {
		return ses.selectList(ns + "selectName");
	}
}

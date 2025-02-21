package com.pretender.myApp.persistence;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pretender.myApp.model.CastLikeCategoryDTO;
import com.pretender.myApp.model.CollectionItemDTO;

@Component
public class MediaInfoDAO {
	@Autowired
	private SqlSession ses;
	private String ns = "com.pretender.myApp.mapper.mediaInfoMapper.";
	
	public List<CastLikeCategoryDTO> getCastLikeCategory() {
		return ses.selectList(ns +"selectCastLikeCategory");
	}
}
package com.pretender.myApp.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public Float getAverageStars(String mediaId, String mediaType) {
		Map<String, Object> params = new HashMap<>();
		params.put("mediaId", Integer.valueOf(mediaId));
		params.put("mediaType", mediaType);
		return ses.selectOne(ns + "selectAverageStars", params);
	}
}
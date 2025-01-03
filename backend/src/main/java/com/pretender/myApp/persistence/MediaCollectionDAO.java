package com.pretender.myApp.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pretender.myApp.model.CollectionItemDTO;

@Component
public class MediaCollectionDAO {
	@Autowired
	private SqlSession ses;
	private String ns = "com.pretender.myApp.mapper.mediaCollectionMapper.";
	
	public int countWatchListById(String memberId) {
		return ses.selectOne(ns +"countWatchListById", memberId);
	}
	
	public int createWatchList(String memberId) {
		return ses.insert(ns + "insertWatchListOfMember", memberId);
	}
	
	public List<CollectionItemDTO> getWatchListById(String memberId) {
		return ses.selectList(ns +"selectWatchListById", memberId);
	}

	public int countMediaInWatchList(String memberId, String mediaId, String mediaType) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("mediaId", mediaId);
		params.put("mediaType", mediaType);
		return ses.selectOne(ns + "countMediaInWatchList", params);
	}

	public int addItemInWatchList(String memberId, String mediaId, String mediaType, String mediaTitle) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("mediaId", mediaId);
		params.put("mediaType", mediaType);
		params.put("mediaTitle", mediaTitle);
		return ses.insert(ns + "insertItemInWatchList", params);
	}
}

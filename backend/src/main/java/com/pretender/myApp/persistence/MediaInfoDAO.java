package com.pretender.myApp.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pretender.myApp.model.CastLikeCategoryDTO;
import com.pretender.myApp.model.CastVotesDTO;
import com.pretender.myApp.model.CollectionItemDTO;
import com.pretender.myApp.model.VoteReasonsDTO;

@Component
public class MediaInfoDAO {
	@Autowired
	private SqlSession ses;
	private String ns = "com.pretender.myApp.mapper.mediaInfoMapper.";
	
	public List<CastLikeCategoryDTO> getCastLikeCategory() {
		return ses.selectList(ns +"selectCastLikeCategory");
	}

	public List<CastVotesDTO> getCastVotesInfo(String mediaId, String type) {
		// characterId, name, actorname, votes 가져오기
		HashMap<String,Object> map = new HashMap<>();
		map.put("mediaId", mediaId);
		map.put("type", type);
		return ses.selectList(ns + "selCastVotes",map);
	}

	public List<VoteReasonsDTO> getVotesResons(String mediaId, String type, String characterId) {
		// content, votes by characterId 가져오기
		HashMap<String, Object> map = new HashMap<>();
		map.put("mediaId", mediaId);
		map.put("type", type);
		map.put("characterId", characterId);
		return ses.selectList(ns+"selVoteReasons", map);
  }
	
	public Float getAverageStars(String mediaId, String mediaType) {
		Map<String, Object> params = new HashMap<>();
		params.put("mediaId", Integer.valueOf(mediaId));
		params.put("mediaType", mediaType);
		return ses.selectOne(ns + "selectAverageStars", params);
	}
}
package com.pretender.myApp.persistence;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pretender.myApp.model.AISummaryCategoryDTO;
import com.pretender.myApp.model.AISummaryDTO;
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
	
	public int getVote(String memberId, String mediaId, String type) {
		Map<String, Object> params = Map.of("memberId", memberId, "mediaId", mediaId, "type", type);
		return ses.selectOne(ns + "selectVote", params);
	}
	
	public int vote(String memberId, String mediaId, String characterId, String type, int why) {
		Map<String, Object> params = Map.of("memberId", memberId, "mediaId", mediaId, "characterId", characterId, "type", type, "why", why);
		return ses.insert(ns + "insertVote", params);
	}
	
	public Float getAverageStars(String mediaId, String mediaType) {
		Map<String, Object> params = new HashMap<>();
		params.put("mediaId", Integer.valueOf(mediaId));
		params.put("mediaType", mediaType);
		return ses.selectOne(ns + "selectAverageStars", params);
	}

	public AISummaryDTO getAiSummary(String mediaId, String type) {
		HashMap<String,Object> params = new HashMap<>();
		params.put("mediaId", Integer.parseInt(mediaId));
		params.put("type", type);
		
		AISummaryDTO result = ses.selectOne(ns + "selectAiSummary", params);
		System.out.println("result : " + result);
		return result;
	}

	public Map<Integer, String> getAiSummaryCategory() {
		Map<Integer, AISummaryCategoryDTO> map = ses.selectMap(ns + "selectAiSummaryCategory", "categoryNumber");
		Map<Integer, String> result = new HashMap<>();
	    for (Map.Entry<Integer, AISummaryCategoryDTO> entry : map.entrySet()) {
	    	result.put(entry.getKey(), entry.getValue().getCategory());
	    }
		return result;
	}

	public List<String> getAiSummaryProvidedList() {
		return ses.selectList(ns + "selectAiSummaryProvidedList");
	}

	public List<Map<String, String>> getDetailForAiSummaryProvidedList(List<Integer> idList) {
		return ses.selectList(ns + "selectDetailForAiSummaryProvidedList", idList);
	}
}
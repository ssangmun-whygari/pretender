package com.pretender.myApp.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pretender.myApp.model.MembersDTO;
import com.pretender.myApp.model.MyActivitiesDTO;
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
	
	public String getProfileImageName(String memberId) {
		return ses.selectOne(ns + "selectProfileImageName", memberId);
	}
	
	public int updateProfileImageName(String memberId, String fileName) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("fileName", fileName);
		return ses.update(ns + "updateProfileImageName", params);
	}

	public List<MyActivitiesDTO> selectAllMyActivities(String userId, int startNo, int size) {
		// myActivities불러오기
		Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("startNo", startNo);
		map.put("size", size);
		return ses.selectList(ns +"selectMyActivities", map);
	}

	public int getMyTotalActivities(String userId) {
		// myActivities의 총개수
		return ses.selectOne(ns+"countMyActivities", userId);
	}

	public int getMyTotalSearchAct(String userId, String word) {
		// 검색결과의 수
		Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("word", word);
		return ses.selectOne(ns+"countSearchRst", map);
	}

	public List<MyActivitiesDTO> searchMyActivities(String userId, String word, int startNo, int size) {
		// 검색결과
		Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("word", word);
		map.put("startNo", startNo);
		map.put("size", size);
		return ses.selectList(ns+"searchMyActvt", map);
	}

}

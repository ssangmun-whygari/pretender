package com.pretender.myApp.persistence;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pretender.myApp.model.CollectionItemDTO;
import com.pretender.myApp.model.CommentsDTO;
import com.pretender.myApp.model.CommentsVO;
import com.pretender.myApp.model.ReviewDTO;
import com.pretender.myApp.model.ReviewLikesDTO;

@Component
public class CommentsDAO {
	
	@Autowired
	private SqlSession ses;
	private String ns ="com.pretender.myApp.mapper.CommentsMapper.";

	public List<CommentsVO> getAllTheComments(int id, int startNo, int size, String sortBy) {
		// 모든 댓글 가져오기	    
		HashMap<String, Object> paging = new HashMap<>();
		paging.put("id",id);
		paging.put("startNo",startNo);
		paging.put("size", size);
		paging.put("sortBy", sortBy); 
		return ses.selectList(ns+"selectAllCmnts", paging);
	}

	public List<CommentsDTO> getAllTheReplies(int id, int parentId, int startNo, int lastNo) {
		// 모든 대댓글 가져오기
		HashMap<String, Integer> paging = new HashMap<>();
		paging.put("id", id);
		paging.put("parentId", parentId);
		paging.put("startNo", startNo);
		paging.put("lastNo", lastNo);
		return ses.selectList(ns+"selectAllRpls",paging);
	}

	public int insertMyReviewLike(ReviewLikesDTO likeEle) {
		// 좋아요 테이블에 삽입
		return ses.insert(ns+"insertReviewLk", likeEle);
	}

	public int deleteMyReviewLike(ReviewLikesDTO likeEle) {
		// 좋아요 테이블에서 삭제
		return ses.delete(ns+"deleteReviewLk", likeEle);
	}
	
	public int insertIntoTheReview(ReviewDTO review) {
		// 댓글 저장
		return ses.insert(ns+"insertRvw", review);
	}

	public int deleteTheReview(int id, int no) {
		// 댓글 삭제
		HashMap<String, Integer> idAndNo = new HashMap<>();
		idAndNo.put("id", id);
		idAndNo.put("no", no);
		return ses.update(ns+"deleteRvw", idAndNo);
	}

	public int modifyTheReview(ReviewDTO review) {
		// 댓글 수정
		return ses.delete(ns+"modifyRvw", review);
	}

	public List<Integer> selectAllMyReviewLikes(String userId, int contentId) {
		// 내가 좋아요한 댓글 리스트
		HashMap<String,Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("contentId", contentId);
		return ses.selectList(ns+"myReviewlikeList", map);
	}

	public int countAllTheComments(int id) {
		// 댓글 개수 가져오기
		return ses.selectOne(ns+"countAllCmnts", id);
	}

	


	
	
	

}

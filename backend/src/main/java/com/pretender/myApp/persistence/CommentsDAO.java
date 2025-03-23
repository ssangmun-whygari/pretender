package com.pretender.myApp.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pretender.myApp.model.CollectionItemDTO;
import com.pretender.myApp.model.CommentsDTO;
import com.pretender.myApp.model.CommentsVO;
import com.pretender.myApp.model.ReportDTO;
import com.pretender.myApp.model.ReviewDTO;
import com.pretender.myApp.model.ReviewLikesDTO;

@Component
public class CommentsDAO {
	
	@Autowired
	private SqlSession ses;
	private String ns ="com.pretender.myApp.mapper.CommentsMapper.";

	public List<CommentsVO> getAllTheComments(int id, int startNo, int size, String sortBy, String type) {
		// 모든 댓글 가져오기	    
		HashMap<String, Object> paging = new HashMap<>();
		paging.put("id",id);
		paging.put("startNo",startNo);
		paging.put("size", size);
		paging.put("sortBy", sortBy); 
		paging.put("type", type);
		return ses.selectList(ns+"selectAllCmnts", paging);
	}

	public List<CommentsDTO> getAllTheReplies(int id, int parentId, int startNo, int size, String type) {
		// 모든 대댓글 가져오기
		HashMap<String, Object> paging = new HashMap<>();
		paging.put("id", id);
		paging.put("parentId", parentId);
		paging.put("startNo", startNo);
		paging.put("size", size);
		paging.put("type", type);
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

	public int deleteTheReview(int id, int no, String mediaType) {
		// 댓글 삭제
		HashMap<String, Object> idAndNo = new HashMap<>();
		idAndNo.put("id", id);
		idAndNo.put("no", no);
		idAndNo.put("mediaType", mediaType);
		return ses.update(ns+"deleteRvw", idAndNo);
	}

	public int modifyTheReview(ReviewDTO review) {
		// 댓글 수정
		return ses.delete(ns+"modifyRvw", review);
	}

	public List<Integer> selectAllMyReviewLikes(String userId, int contentId, String mediaType) {
		// 내가 좋아요한 댓글 리스트
		HashMap<String,Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("contentId", contentId);
		map.put("mediaType", mediaType);
		return ses.selectList(ns+"myReviewlikeList", map);
	}

	public int countAllTheComments(int id, String type) {
		// 댓글 개수 가져오기
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("type", type);
		return ses.selectOne(ns+"countAllCmnts",map);
	}

	public int insertTheReport(ReportDTO report) {
		// 신고하기
		return ses.insert(ns+"insertRprt", report);
	}

	public Object selectSameReportInfo(ReportDTO report) {
		// 신고 중복 체크
		return ses.selectOne(ns+"sameRprt", report);
	}

	public int selectCommentIndex(int contentId, int commentId, String type) {
		// 댓글 인덱스 가져오기
		HashMap<String, Object> map = new HashMap<>();
		map.put("contentId", contentId);
		map.put("commentId", commentId);
		map.put("type", type);
		return ses.selectOne(ns+"commentIdx",map);
	}

	public int selectReplyIndex(int contentId, int commentId, Integer replyId, String type) {
		// 대댓글 인덱스 가져오기
		HashMap<String, Object> map = new HashMap<>();
		map.put("contentId", contentId);
		map.put("commentId", commentId);
		map.put("replyId", replyId);
		map.put("type", type);
		return ses.selectOne(ns+"replyIdx",map);
	}

	public int postReview(int no, String userId, String mediaId, String mediaType, String text, Float stars) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("no", no);
		map.put("membersId", userId);
		map.put("mediaId", Integer.valueOf(mediaId));
		map.put("mediaType", mediaType);
		map.put("text", text);
		map.put("stars", stars);
		return ses.insert(ns + "insertReview", map);
	}
	
	public int getAvailableNoFromReviewsTable(String mediaId, String mediaType) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("mediaId", Integer.valueOf(mediaId));
		map.put("mediaType", mediaType);
		return ses.selectOne(ns + "selectAvailableNoFromReviewsTable", map);
	}

	

}

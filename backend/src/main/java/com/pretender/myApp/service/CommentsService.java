package com.pretender.myApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pretender.myApp.model.CollectionItemDTO;
import com.pretender.myApp.model.CommentsDTO;
import com.pretender.myApp.model.CommentsVO;
import com.pretender.myApp.model.ReportDTO;
import com.pretender.myApp.model.ReviewDTO;
import com.pretender.myApp.model.ReviewLikesDTO;
import com.pretender.myApp.persistence.CommentsDAO;

@Service
public class CommentsService {
	
	@Autowired
	private CommentsDAO cDao;

	public List<CommentsVO> getAllComments(int id, int page, int size, String sortBy, String type) {
		// 모든 댓글 최신순으로 불러오기
		int startNo = page * size;
		return cDao.getAllTheComments(id,startNo,size,sortBy, type);
	}
	
	public boolean postReview(String userId, String mediaId, String mediaType, String text, Float stars) throws Exception {
		int no = cDao.getAvailableNoFromReviewsTable(mediaId, mediaType);
		int result = cDao.postReview(no, userId, mediaId, mediaType, text, stars);
		if (result != 1) { // 삽입된 행의 개수는 1이어야 함
			throw new Exception("reviews 테이블에 행이 제대로 업데이트되지 않았습니다.");
		}
		return true;
	}
	

	public List<CommentsDTO> getAllReplies(int id, int parentId, int page, int size, String type) {
		// 모든 대댓글 최신순으로 불러오기
		int startNo = page * size;
		List<CommentsDTO> replies = cDao.getAllTheReplies(id,parentId,startNo,size, type);
		return replies;
	}


	public int insertReviewLike(ReviewLikesDTO likeEle) {
		// 리뷰 좋아요 삽입
		return cDao.insertMyReviewLike(likeEle);		
	}


	public int deleteReviewLike(ReviewLikesDTO likeEle) {
		// 리뷰 좋아요 삭제
		return cDao.deleteMyReviewLike(likeEle);
	}

	public int insertIntoReview(ReviewDTO review) {
		// 댓글 달기
		return cDao.insertIntoTheReview(review);
	}


	public int deleteMyReview(int id, int no, String mediaType) {
		// 댓글 삭제
		return cDao.deleteTheReview(id,no, mediaType);
		
	}


	public int modifyMyReview(ReviewDTO review) {
		// 댓글 수정
		return cDao.modifyTheReview(review);
	}


	public List<Integer> selectmyReviewLike(String userId, int contentId, String mediaType) {
		// 내가 좋아요한 댓글 목록
		List<Integer> likeLists = cDao.selectAllMyReviewLikes(userId,contentId, mediaType);
		return likeLists;
	}


	public int getTotalComments(int id, String type) {
		// 댓글 총개수
		int totalComments = cDao.countAllTheComments(id, type);
		return totalComments;
	}


	public int reportAComment(ReportDTO report) {
		// 댓글 신고
		return cDao.insertTheReport(report);
	}


	public Object checkDupeBeforeReport(ReportDTO report) {
		// 신고 중복 체크
		return cDao.selectSameReportInfo(report);
	}


	public int findCommentIndex(int contentId, int commentId, String type) {
		// 댓글의 인덱스 가져오기(좋아요순)
		return cDao.selectCommentIndex(contentId, commentId, type);
	}


	public int findReplyIndex(int contentId, int commentId, Integer replyId, String type) {
		// 대댓글의 인덱스 가져오기
		return cDao.selectReplyIndex(contentId, commentId, replyId, type);
	}


	
}
package com.pretender.myApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pretender.myApp.model.CollectionItemDTO;
import com.pretender.myApp.model.CommentsDTO;
import com.pretender.myApp.model.CommentsVO;
import com.pretender.myApp.model.ReviewDTO;
import com.pretender.myApp.model.ReviewLikesDTO;
import com.pretender.myApp.persistence.CommentsDAO;

@Service
public class CommentsService {
	
	@Autowired
	private CommentsDAO cDao;

	public List<CommentsVO> getAllComments(int id, int page, int size, String sortBy) {
		// 모든 댓글 최신순으로 불러오기
		int startNo = page * size;
		return cDao.getAllTheComments(id,startNo,size,sortBy);
	}
	

	public List<CommentsDTO> getAllReplies(int id, int parentId, int page, int size) {
		// 모든 대댓글 최신순으로 불러오기
		int startNo = page * size;
		List<CommentsDTO> replies = cDao.getAllTheReplies(id,parentId,startNo,size);
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


	public int deleteMyReview(int id, int no) {
		// 댓글 삭제
		return cDao.deleteTheReview(id,no);
		
	}


	public int modifyMyReview(ReviewDTO review) {
		// 댓글 수정
		return cDao.modifyTheReview(review);
	}


	public List<Integer> selectmyReviewLike(String userId, int contentId) {
		// 내가 좋아요한 댓글 목록
		List<Integer> likeLists = cDao.selectAllMyReviewLikes(userId,contentId);
		return likeLists;
	}


	public int getTotalComments(int id) {
		// 댓글 총개수
		int totalComments = cDao.countAllTheComments(id);
		return totalComments;
	}


	
}
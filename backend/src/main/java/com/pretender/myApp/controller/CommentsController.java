package com.pretender.myApp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pretender.myApp.model.CommentsDTO;
import com.pretender.myApp.model.CommentsVO;
import com.pretender.myApp.model.ReportDTO;
import com.pretender.myApp.model.ReviewDTO;
import com.pretender.myApp.model.ReviewLikesDTO;
import com.pretender.myApp.service.CommentsService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class CommentsController {
	
	@Autowired
	private CommentsService cService;
	
	private static final int PAGE_SIZE_COMMENTS = 5;
	private static final int PAGE_SIZE_REPLIES = 10;
	
	// 모든 코멘트 가져오기 (페이지네이션)
	@GetMapping("/api/comments")
	ResponseEntity<Object> getComments(@RequestParam int id, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "likeCount") String sortBy){
		int size = PAGE_SIZE_COMMENTS;
		int totalComments = cService.getTotalComments(id);
		int totalPages = (int) Math.ceil((double) totalComments / size);
		List<CommentsVO> comments = cService.getAllComments(id,page,size,sortBy);
		Map<String, Object> response = new HashMap<>();
		response.put("totalComments", totalComments);
		response.put("totalPages", totalPages);
		response.put("page", page);
		response.put("size", size);
		response.put("comments", comments);
		return ResponseEntity.ok(response);
	}
	
	// 대댓글 기져오기
	@PostMapping("/api/replies")
	ResponseEntity<Object> getReplies(@RequestParam int id, @RequestParam(defaultValue = "0") int page, int parentId, @RequestParam int total){
		int size = PAGE_SIZE_REPLIES;
		Map<String, Object> response = new HashMap<>();
		List<CommentsDTO> replies = cService.getAllReplies(id,parentId,page,size);
		response.put("page", page);
		response.put("size", size);
		response.put("parentId", parentId);
		response.put("replies", replies);
		return ResponseEntity.ok(response);
	}
	
	//좋아요 기능
	@PostMapping("api/reviewLike")
	public ResponseEntity<Object> insertReviewLike(@RequestBody ReviewLikesDTO likeEle) {
		
		Authentication auth =SecurityContextHolder.getContext().getAuthentication();	
		
		 if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			  return ResponseEntity.badRequest().body("로그인이 필요합니다.");
	        }
		 
		String userId = auth.getName();
		int id = likeEle.getMediaId();
		
		likeEle.setMediaType("tv");
		likeEle.setMembersId(userId);
		
		if(cService.insertReviewLike(likeEle) == 1) {
			return ResponseEntity.ok(userId+"가 " + likeEle.getReviewsNo() + "번 리뷰를 좋아합니다.");
		}
		return ResponseEntity.badRequest().body("리뷰 좋아요가 실패했습니다.");
	}
	
	//좋아요 취소
	@DeleteMapping("api/reviewUnlike")
	public ResponseEntity<Object> deleteReviewLike(@RequestParam int id, @RequestParam int no){
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		  if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			  return ResponseEntity.badRequest().body("로그인이 필요합니다.");
	        }
		String userId = auth.getName();
		ReviewLikesDTO likeEle = new ReviewLikesDTO();
		likeEle.setMediaId(id);
		likeEle.setReviewsNo(no);
		likeEle.setMembersId(userId);
		System.out.println("likeEle:"+likeEle.toString());
		
		if(cService.deleteReviewLike(likeEle) == 1) {
			return ResponseEntity.ok("사용자가 리뷰 좋아요를 취소했습니다.");
		}
		
		return ResponseEntity.badRequest().body("리뷰 좋아요 취소에 실패했습니다.");
	}
	
	//좋아요 조회
	@GetMapping("/api/myReviewLikes")
	public ResponseEntity<Object> selectReviewLike(int contentId){
	  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String userId = authentication.getName();
        
        List<Integer> myLikes = cService.selectmyReviewLike(userId,contentId);
        
        return ResponseEntity.ok(myLikes);
	}
	//댓글 작성 //setType부분은 다시 해줘야 함
	@PostMapping("api/insertReview")
	public ResponseEntity<Object> insertReview(int id, @RequestBody CommentsDTO comment){
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		 if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			  return ResponseEntity.badRequest().body("로그인이 필요합니다.");
	        }
		 
		String membersId = auth.getName();
		int parentNo = comment.getParent_no();
		String content = comment.getContent();
		
		ReviewDTO review = new ReviewDTO();
		review.setMembersId(membersId);
		review.setParentNo(parentNo);
		review.setContent(content);
		review.setId(id);
		review.setType("tv");
		
		if(parentNo == 0) {
			review.setIsParent('Y');
		}else {
			review.setIsParent('N');
		}
	
		if(cService.insertIntoReview(review) == 1) {
			return ResponseEntity.ok("댓글이 성공적으로 저장되었습니다.");
		}else {
			return ResponseEntity.badRequest().body("댓글 저장에 실패했습니다.");
		}
	}
	
	//댓글 삭제
	@PutMapping("api/deleteReview")
	public ResponseEntity<Object> deleteReview(
	    @RequestParam int id,
	    @RequestParam int no,
	    @RequestParam String membersId
	) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
	        return ResponseEntity.badRequest().body("로그인이 필요합니다.");
	    }

	    String userId = auth.getName();
	    if (!membersId.equals(userId)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다");
	    }

	    if (cService.deleteMyReview(id, no) == 1) {
	        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
	    }

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리뷰 삭제에 실패했습니다.");
	}

	
	//댓글 수정
	@PutMapping("api/modifyReview")
	public ResponseEntity<Object> modifyReview(@RequestBody ReviewDTO review){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();	
		
		 if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			  return ResponseEntity.badRequest().body("로그인이 필요합니다.");
	        }
		 if(review.getIsDeleted() == 'Y') {
			  return ResponseEntity.badRequest().body("이미 삭제한 글입니다.");
		 	}
		 
		String userId = auth.getName();
		String membersId = review.getMembersId();
		
		 if(!membersId.equals(userId)) {
		    	return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다");
		    }
	    review.setMembersId(userId);

	    if (cService.modifyMyReview(review) > 0) {
	        return ResponseEntity.ok("리뷰가 수정되었습니다.");
	    }
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리뷰 수정에 실패했습니다.");
	}
	
	// 사용자 아이디 가져오기
	@GetMapping("api/getLoggedInId")
	public ResponseEntity<Object> getLoggedInUserId(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userId = auth.getName();
		return ResponseEntity.ok(userId);
		
	}
	
	
	
	//댓글 신고 //setType부분은 수정 예정
	@PostMapping("api/report")
	public ResponseEntity<Object> reportComment (@RequestBody ReportDTO report) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	
	        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	        }
	
	        String userId = authentication.getName();
	        report.setReporterId(userId);
	        report.setMediaType("tv");
	        
			cService.reportAComment(report);
			
			return ResponseEntity.status(HttpStatus.OK).body("댓글이 신고되었습니다.");
		}catch(DuplicateKeyException e) {
			return ResponseEntity.badRequest().body("이미 신고한 댓글입니다."); // 테이블에 복합키 설정 후에 작동 예정
		}
	}
	

}

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
	
	// 모든 코멘트 가져오기 (페이지네이션) //type update
	@GetMapping("/api/comments")
	ResponseEntity<Object> getComments(@RequestParam int id, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "likeCount") String sortBy, @RequestParam String mediaType){
		int size = PAGE_SIZE_COMMENTS;
		int totalComments = cService.getTotalComments(id,mediaType);
		int totalPages = (int) Math.ceil((double) totalComments / size);
		List<CommentsVO> comments = cService.getAllComments(id,page,size,sortBy,mediaType);
		Map<String, Object> response = new HashMap<>();
		response.put("totalComments", totalComments);
		response.put("totalPages", totalPages);
		response.put("page", page);
		response.put("size", size);
		response.put("comments", comments);
		return ResponseEntity.ok(response);
	}
	
	// 단일 리뷰 DB에 삽입
	@PostMapping("/api/review")
	ResponseEntity<Object> postReview(
			Authentication token,
			@RequestParam String mediaId,
			@RequestParam String mediaType,
			@RequestParam String text,
			@RequestParam Float stars
			) {
		System.out.println("POST /api/review");
		System.out.println("token : " + token);
		System.out.println("token.getName() : " + token.getName());
		System.out.println("mediaId : " + mediaId);
		System.out.println("mediaType : " + mediaType);
		System.out.println("text : " + text);
		System.out.println("stars : " + stars);
		String userId = token.getName();
		
		Map<String, Object> response = new HashMap<>();
		try {
			cService.postReview(userId, mediaId, mediaType, text, stars);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			response.put("result", "fail");
			return ResponseEntity.internalServerError().body(response);
		}
		response.put("result", "success");
		return ResponseEntity.ok(response);
	}
	
	// 대댓글 기져오기 //type update
	@PostMapping("/api/replies")
	ResponseEntity<Object> getReplies(@RequestParam int id, @RequestParam(defaultValue = "0") int page, int parentId, 
			@RequestParam int total, @RequestParam String mediaType){
		int size = PAGE_SIZE_REPLIES;
		Map<String, Object> response = new HashMap<>();
		List<CommentsDTO> replies = cService.getAllReplies(id,parentId,page,size,mediaType);
		response.put("page", page);
		response.put("size", size);
		response.put("parentId", parentId);
		response.put("replies", replies);
		return ResponseEntity.ok(response);
	}
	
	//좋아요 기능 // type update
	@PostMapping("api/reviewLike")
	public ResponseEntity<Object> insertReviewLike(@RequestBody ReviewLikesDTO likeEle) {
		
		Authentication auth =SecurityContextHolder.getContext().getAuthentication();	
		
		 if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			  return ResponseEntity.badRequest().body("로그인이 필요합니다.");
	        }
		 
		String userId = auth.getName();
		int id = likeEle.getMediaId();
		
		likeEle.setMembersId(userId);
		
		if(cService.insertReviewLike(likeEle) == 1) {
			return ResponseEntity.ok(userId+"가 " + likeEle.getReviewsNo() + "번 리뷰를 좋아합니다.");
		}
		return ResponseEntity.badRequest().body("리뷰 좋아요가 실패했습니다.");
	}
	
	//좋아요 취소 //type update
	@DeleteMapping("api/reviewUnlike")
	public ResponseEntity<Object> deleteReviewLike(@RequestParam int mediaId, @RequestParam int reviewsNo, @RequestParam String mediaType){
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		  if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			  return ResponseEntity.badRequest().body("로그인이 필요합니다.");
	        }
		String userId = auth.getName();
		ReviewLikesDTO likeEle = new ReviewLikesDTO();
		likeEle.setMediaId(mediaId);
		likeEle.setReviewsNo(reviewsNo);
		likeEle.setMembersId(userId);
		likeEle.setMediaType(mediaType);
		System.out.println("likeEle:"+likeEle.toString());
		
		if(cService.deleteReviewLike(likeEle) == 1) {
			return ResponseEntity.ok("사용자가 리뷰 좋아요를 취소했습니다.");
		}
		
		return ResponseEntity.badRequest().body("리뷰 좋아요 취소에 실패했습니다.");
	}
	
	//좋아요 조회 //Type update
	@GetMapping("/api/myReviewLikes")
	public ResponseEntity<Object> selectReviewLike(@RequestParam int contentId,@RequestParam String mediaType){
	  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String userId = authentication.getName();
        
        List<Integer> myLikes = cService.selectmyReviewLike(userId,contentId, mediaType);
        
        return ResponseEntity.ok(myLikes);
	}
	//댓글 작성 // type update
	@PostMapping("api/insertReview")
	public ResponseEntity<Object> insertReview(@RequestParam int id, @RequestParam String mediaType, @RequestBody CommentsDTO comment){
		
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
		review.setType(mediaType);
		
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
	
	//댓글 삭제 // type update
	@PutMapping("api/deleteReview")
	public ResponseEntity<Object> deleteReview(
	    @RequestParam int id,
	    @RequestParam int no,
	    @RequestParam String membersId,
	    @RequestParam String mediaType
	) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
	        return ResponseEntity.badRequest().body("로그인이 필요합니다.");
	    }

	    String userId = auth.getName();
	    if (!membersId.equals(userId)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다");
	    }

	    if (cService.deleteMyReview(id, no, mediaType) == 1) {
	        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
	    }

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리뷰 삭제에 실패했습니다.");
	}

	
	//댓글 수정 // type update
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
	
	
	
	//댓글 신고 //type update
	@PostMapping("api/report")
	public ResponseEntity<Object> reportComment (@RequestBody ReportDTO report) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	
	        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	        }
	
	        String userId = authentication.getName();
	        report.setReporterId(userId);
	        
			cService.reportAComment(report);
			
			return ResponseEntity.status(HttpStatus.OK).body("댓글이 신고되었습니다.");
		}catch(DuplicateKeyException e) {
			return ResponseEntity.status(HttpStatus.OK).body("이미 신고한 댓글입니다."); // 테이블에 복합키 설정 후에 작동 예정
		}
	}
	
	//신고 중복 체크 // type update
	@PostMapping("api/checkBeforeReport")
	public ResponseEntity<Object> checkBeforeReport (@RequestBody ReportDTO report) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String userId = authentication.getName();
        report.setReporterId(userId);
        
        if(cService.checkDupeBeforeReport(report) != null) {
        	return ResponseEntity.status(HttpStatus.OK).body("이미 신고한 댓글입니다.");
        }
        return ResponseEntity.status(HttpStatus.OK).body("신고폼을 작성해주세요.");
	}
	
	//comment의 인덱스와 reply의 인덱스 가져오기/ type update
	@GetMapping("/api/commentPage")
	public ResponseEntity<Map<String, Integer>> getCommentPage(
	    @RequestParam("id") int contentId,
	    @RequestParam("commentId") int commentId,
	    @RequestParam(value="replyId", required = false) Integer replyId,
	    @RequestParam String mediaType) {
	    
	    int pageSize = PAGE_SIZE_COMMENTS;
	    //댓글 인덱스
	    int commentIndex = cService.findCommentIndex(contentId, commentId, mediaType);
	    int commentPage = Math.max(0, (commentIndex - 1) / pageSize);
	    //대댓글 인엑스
	    Integer replyPage = null;
	    int replyPageSize =PAGE_SIZE_REPLIES;
	    if (replyId != null) { 
	        int replyIndex = cService.findReplyIndex(contentId, commentId, replyId, mediaType);
	        replyPage = ( replyIndex/ replyPageSize );
	    }
	    
	    Map<String, Integer> response = new HashMap<>();
	    response.put("index", commentIndex);
	    response.put("commentPage", commentPage);
	    response.put("replyPage", replyPage);

	    return ResponseEntity.ok(response);
	}
	

}

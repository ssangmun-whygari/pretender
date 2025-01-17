package com.pretender.myApp.model;

import java.sql.Date;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentsVO {

	private int no;
	private String members_id;
	private Character isParent;
	private int parent_no;
	private String content;
	private LocalDateTime post_date;
	private LocalDateTime correct_date;
	private Character is_deleted;
	private String image;
	private String nickname;
	private int likeCount;
	private int count;
	private float stars;
	private int replyCount;
}

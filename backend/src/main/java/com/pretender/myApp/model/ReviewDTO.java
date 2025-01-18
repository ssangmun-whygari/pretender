package com.pretender.myApp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
	
	private int no;
	private String membersId;
	private Character isParent;
	private int parentNo;
	private String content;
	private int id;
	private char isDeleted; 
	private String type;
}

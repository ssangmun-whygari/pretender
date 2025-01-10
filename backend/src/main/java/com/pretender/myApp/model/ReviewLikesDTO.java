package com.pretender.myApp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLikesDTO {
	int mediaId;
	String mediaType;
	int reviewsNo;
	String membersId;
}

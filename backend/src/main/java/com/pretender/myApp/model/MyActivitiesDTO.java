package com.pretender.myApp.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MyActivitiesDTO {
	private String mediaTitle;
	private String content;
	private LocalDateTime postDate;
	private LocalDateTime correctDate;
	private String isDeleted;
	private int mediaId;
	private String mediaType;
	private int no;
	private int parentNo;
}

package com.pretender.myApp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
	String reporterId;
	int reviewsNo;
	int mediaId;
	String mediaType;
	int cause;
	String message;
}

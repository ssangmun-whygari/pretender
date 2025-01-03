package com.pretender.myApp.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CollectionItemDTO {
	private int mediaId;
	private String mediaType;
	private String mediaTitle;
	private LocalDateTime addedDate;
	private String posterPath;
}

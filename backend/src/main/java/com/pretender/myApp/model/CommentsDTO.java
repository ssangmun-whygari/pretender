package com.pretender.myApp.model;

import java.sql.Date;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentsDTO {
	private int no;  
	private String members_id;
    private Integer parent_no;    
    private String content;      
    private LocalDateTime post_date; 
    private boolean isDeleted;   
    private String image;        
    private String nickname;     
    private int likeCount; 

}

package com.pretender.myApp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CastVotesDTO {
	private String character_name;
	private String actor_name;
	private int character_id;
	private int votes;
}

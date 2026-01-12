package com.pretender.myApp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VoteRequestDTO {
    private String memberId;
    private String mediaId;
    private String characterId;
    private String type;
    private int why;
}

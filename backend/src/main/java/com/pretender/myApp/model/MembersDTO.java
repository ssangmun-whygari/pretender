package com.pretender.myApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MembersDTO {
    private String id;
    private String password;
    private String birthday;
    private String gender;
    private String registerDate; 
    private String status;
    private String nickname;
    private String image;
    private String isAdmin;
}
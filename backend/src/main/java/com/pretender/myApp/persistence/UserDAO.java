package com.pretender.myApp.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.pretender.myApp.vodto.UserDTO;

@Mapper
public interface UserDAO {
	 // 이메일 중복 체크
	 int countById(@Param("id") String id);

	 // 회원 정보 저장
	 void insertUser(UserDTO userDTO);
}

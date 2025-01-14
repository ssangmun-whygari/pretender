package com.pretender.myApp.service;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pretender.myApp.model.MembersDTO;
import com.pretender.myApp.persistence.MembersDAO;


@Service
public class MembersService {
		
	@Autowired
	private MembersDAO membersDAO;

	private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    //비밀번호 검증 패턴 (6~15자, 대문자, 소문자, 숫자, 특수문자 포함)
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{6,15}$";

    public int registerUser(MembersDTO membersDTO) {
        if (!isValidEmail(membersDTO.getId())) {
            throw new IllegalArgumentException("아이디는 이메일 형식이어야 합니다.");
        }

        if (!isValidPassword(membersDTO.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 6~15자, 대문자, 소문자, 숫자, 특수문자 포함이어야 합니다.");
        }

        if(isDuplicateId(membersDTO.getId())){
          throw new IllegalArgumentException("이미 가입한 회원입니다.");
        }

        if(isDuplicateNickname(membersDTO.getNickname())) {
          throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        membersDTO.setStatus("active");
        membersDTO.setIsAdmin("0");

        return membersDAO.insertUser(membersDTO);
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    private boolean isValidPassword(String password) {
        return Pattern.matches(PASSWORD_REGEX, password);
    }

    private boolean isDuplicateId(String email) {
      return membersDAO.countById(email) > 0;
    }

    private boolean isDuplicateNickname(String nickname) {
      return membersDAO.countByNickname(nickname) > 0;
    }
    
    @Transactional
    public void updateProfileImage(String memberId, MultipartFile imageFile) throws Exception {
    	File imageSaveDir = new File("images/members/");
        System.out.println("파일 저장 디렉토리: " + imageSaveDir.getAbsolutePath());
        
        // TODO : S3 저장소에서 파일을 쓰고 지우는 걸로 바꿔야 함. 일단 임시로 서버를 저장소로 사용
        // 기존의 파일 삭제
        String oldFilename = membersDAO.getProfileImageName(memberId);
        if (!("default_profile.png").equals(oldFilename)) {
        	File target = new File(imageSaveDir + "/" + oldFilename);
        	target.delete();
        }
        
        // ID_현재시간의 형식으로 파일이름을 지정
        String newFilename = memberId + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        // 절대경로 사용해야 한다고 함...
        // reference : https://deonggi.tistory.com/123
        imageFile.transferTo(new File(imageSaveDir.getAbsolutePath() + File.separator + newFilename));
        if (membersDAO.updateProfileImageName(memberId, newFilename) != 1) {
        	throw new Exception("DB 업데이트 중 문제 발생");
        }
    }
    
    public Resource getProfileImage(String memberId) throws Exception {
    	String fileName = membersDAO.getProfileImageName(memberId);
    	File imageFile;
    	// 디폴트 프로필 이미지와 유저가 지정한 프로필 이미지의 경로가 다름
    	if (!("default_profile.png").equals(fileName)) {
    		imageFile = new File("images/members/" + fileName);
    	} else {
    		imageFile = new File("images/public/" + fileName);
    	}
    	
    	Resource resource = new UrlResource(Paths.get(imageFile.getAbsolutePath()).normalize().toUri());
    	return resource;
    }
}

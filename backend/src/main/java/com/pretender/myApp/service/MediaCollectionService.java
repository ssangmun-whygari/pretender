package com.pretender.myApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import com.pretender.myApp.model.CollectionItemDTO;
import com.pretender.myApp.persistence.MediaCollectionDAO;

@Service
public class MediaCollectionService {
	@Autowired
	private MediaCollectionDAO mediaCollectionDAO;
	@Autowired
	private MediaInfoService mediaInfoService;
	
	private boolean hasWatchList(String memberId) {
		return (mediaCollectionDAO.countWatchListById(memberId) >= 1);
	}
	
	public List<CollectionItemDTO> getWatchList(String memberId) {
		if (hasWatchList(memberId) == false) {
			mediaCollectionDAO.createWatchList(memberId);
			System.out.println(memberId + "의 기본 콜렉션이 생성되었습니다.");
		}
		return mediaCollectionDAO.getWatchListById(memberId);
	}
	
	public Boolean includesInWatchList(String memberId, String mediaId, String mediaType) {
		if (hasWatchList(memberId) == false) {
			mediaCollectionDAO.createWatchList(memberId);
			System.out.println(memberId + "의 기본 콜렉션이 생성되었습니다.");
			return false; // 기본 콜렉션이 생성된 적도 없으므로 내가 본 리스트에 추가한 적도 없다.
		}
		return mediaCollectionDAO.countMediaInWatchList(memberId, mediaId, mediaType) >= 1;
	}

	// TODO : 예외처리하기
	public boolean addItemInWatchList(String memberId, String mediaId, String mediaType, String mediaTitle) {
//		boolean result = false;
//		mediaInfoService.requestDetail(mediaType, mediaId)
//			.flatMap(responseEntity -> {
//				HttpStatusCode statusCode = responseEntity.getStatusCode();
//				if (statusCode.is2xxSuccessful()) {
//					if (mediaCollectionDAO.addItemInWatchList(memberId, mediaId, mediaType, mediaTitle) == 1) {
//						result = true;
//					} else {
//						result = false;
//					}
//				} else {
//					result = false;
//				}
//			});
//		return result;
		return (mediaCollectionDAO.addItemInWatchList(memberId, mediaId, mediaType, mediaTitle) == 1);
	}
}

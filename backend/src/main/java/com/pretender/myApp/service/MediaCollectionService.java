package com.pretender.myApp.service;

import java.util.List;
import java.util.Map;

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

	public boolean addItemInWatchList(String memberId, String mediaId, String mediaType) {
		boolean result = false;
		try {
			Map<String, Object> response = mediaInfoService.requestDetail(mediaType, mediaId).getBody();
			String mediaTitle = (String) (response.get("name") == null ? response.get("title") : response.get("name"));
			String posterPath = (String) response.get("poster_path");
			if (mediaCollectionDAO.addItemInWatchList(memberId, mediaId, mediaType, mediaTitle, posterPath) == 1) {
				result = true;
			} else {
				throw new Exception("DB에 행이 제대로 삽입되지 않았습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
			return result;
		}
		return result;
	}
}

package com.pretender.myApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pretender.myApp.persistence.SampleDAO;

@Service
public class SampleService {
	@Autowired
	private SampleDAO sampleDAO;
	
	public List<String> getNames() {
		return sampleDAO.selectNames();
	}
}

package com.pretender.myApp.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TMDBclient {
	@Value("${whygari.TMDBtoken}")
	private String token;
	
	@Autowired
	private RestClient.Builder builder;
	
	private volatile RestClient restClient;
	
	public RestClient getRestClient() {
	    if (restClient == null) {
	        synchronized (this) {
	        	if (restClient == null) {
	    			restClient = builder
	    					.baseUrl("https://api.themoviedb.org")
	    					.defaultHeaders(httpHeaders -> {
	    						httpHeaders.add("Authorization", "Bearer " + token);
	    						httpHeaders.add("accept", "application/json");
	    						httpHeaders.add("User-Agent", "Mozilla/5.0");
	    					})
	    					.build();
	        	}
	        }
	    }
		return restClient;
	}
}

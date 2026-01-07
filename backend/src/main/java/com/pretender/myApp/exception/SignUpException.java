package com.pretender.myApp.exception;

import java.util.HashMap;
import java.util.Map;

public class SignUpException extends RuntimeException {
    private final Map<String, Object> body;

    public SignUpException(String errorCode, String msg) {
    	super(msg);
        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", errorCode);
        body.put("message", msg);
        this.body = body;
    }
    
    public Map<String, Object> getBody() {
        return body;
    }
}

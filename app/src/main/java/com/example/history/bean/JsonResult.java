package com.example.history.bean;

public class JsonResult {
    private String status;
    private String message;
    private String sessionId;
    private String data;

    public JsonResult(String status, String message, String sessionId,String data) {
        this.status = status;
        this.message = message;
        this.sessionId = sessionId;
        this.data=data;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSessionId() {
        return sessionId;
    }
    public String getData() {
        return data;
    }

}

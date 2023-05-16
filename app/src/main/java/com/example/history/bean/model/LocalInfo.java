package com.example.history.bean.model;

public class LocalInfo {
    private String username;
    private String imgcover;

    public LocalInfo(String username,String imgcover){
        this.username = username;
        this.imgcover = imgcover;
    }

    public LocalInfo(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImgcover() {
        return imgcover;
    }

    public void setImgcover(String imgcover) {
        this.imgcover = imgcover;
    }
}

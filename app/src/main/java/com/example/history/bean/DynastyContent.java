package com.example.history.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class DynastyContent implements Serializable {
    private Integer id;
    private String content;
    private String img;
    private String title;
    private String brief;


    public DynastyContent(){

    }
    public DynastyContent(Integer id,String title, String brief, String content,String img){
        this.id=id;
        this.content=content;
        this.title=title;
        this.img=img;
        this.brief=brief;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

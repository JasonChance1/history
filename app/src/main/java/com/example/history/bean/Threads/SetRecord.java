package com.example.history.bean.Threads;

import android.util.Log;

import com.example.history.bean.HttpClient;

import java.io.IOException;

public class SetRecord extends Thread{
    private String username;
    private String contentId;
    private String recordType;
    private String option;
    public SetRecord(String username,String contentId,String recordType, String option){
        this.contentId = contentId;
        this.username = username;
        this.recordType = recordType;
        this.option = option;
    }
    @Override
    public void run() {
        try {
            HttpClient.SendMsg(username,contentId,recordType,option);
            Log.d("SetRecord","插入成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.example.history.bean.Threads;

import android.util.Log;

import com.example.history.bean.HttpClient;

import java.io.IOException;

public class SetRecord extends Thread{
    private String username;
    private String contentId;
    private String recordType;
    public SetRecord(String username,String contentId,String recordType){
        this.contentId = contentId;
        this.username = username;
        this.recordType = recordType;
    }
    @Override
    public void run() {
        try {
            HttpClient.SendMsg(username,contentId,recordType);
            Log.d("SetRecord","插入成功");
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}

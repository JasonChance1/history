package com.example.history.bean.Threads;

import android.util.Log;

import com.example.history.bean.HttpClient;

import java.io.IOException;

public class ClearHistory extends Thread{
    String isClear;
    public ClearHistory(String isClear){
        this.isClear = isClear;
    }
    @Override
    public void run() {
        try {
            HttpClient.clearHistory(isClear);
            Log.d("ClearHistory","调用清除历史线程");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ClearHistory","调用清除历史线程异常");
        }
    }
}

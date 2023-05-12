package com.example.history.bean.Threads;

import android.util.Log;

import com.example.history.bean.HttpClient;

import java.io.IOException;
import java.util.concurrent.Callable;

public class NetWorkThread implements Callable<String> {
    public static String username;
    public static String password;

    public NetWorkThread(String username,String password){
        this.username=username;
        this.password=password;
    }


    @Override
    public String call() throws Exception {
        String content;
        content = HttpClient.login(username,password);
        Log.d("NetWorkThread","content---"+content);
        return content;
    }
}

package com.example.history.bean.Threads;

import com.example.history.bean.HttpClient;

import java.io.IOException;

public class ChangePasswordThread extends Thread{
    String username,password;
    public ChangePasswordThread(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public void run() {
        try {
            HttpClient.changeNickname(username,password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

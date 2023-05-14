package com.example.history.bean.Threads;

import com.example.history.bean.HttpClient;

import java.io.IOException;

public class ChangeNicknameThread extends Thread{
    String username,nickname;
    public ChangeNicknameThread(String username,String nickname){
        this.username = username;
        this.nickname = nickname;
    }

    @Override
    public void run() {
        try {
            HttpClient.changeNickname(username,nickname);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

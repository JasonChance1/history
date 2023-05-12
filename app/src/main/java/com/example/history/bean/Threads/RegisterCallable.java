package com.example.history.bean.Threads;

import com.example.history.bean.HttpClient;

import java.util.concurrent.Callable;

public class RegisterCallable implements Callable<String> {
    String username,password,nickname;
    public RegisterCallable(String username, String password, String nickname){
        this.username=username;
        this.password=password;
        this.nickname=nickname;
    }
    @Override
    public String call() throws Exception {
        String result= HttpClient.register(username,password,nickname);
        return result;
    }
}

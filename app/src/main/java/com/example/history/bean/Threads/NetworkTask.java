package com.example.history.bean.Threads;

import android.os.AsyncTask;

import com.example.history.bean.HttpClient;
import com.example.history.bean.JsonResult;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.security.auth.callback.Callback;

public class NetworkTask extends AsyncTask<Void, Void, String> {
    private final String username;
    private final String password;
    private final Callback callback;

    public NetworkTask(String username, String password, Callback callback) {
        this.username = username;
        this.password = password;
        this.callback = callback;
    }
    @Override
    public String doInBackground(Void...voids) {
        String t= null;
        // 在此处执行网络操作
        try {
            t = HttpClient.login(username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (callback != null) {
            try {
                callback.onComplete(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public interface Callback {
        void onComplete(String result) throws JSONException;
    }
    public static int processResult(String result) throws JSONException {
        int k=0;
        Gson gson = new Gson();
        JsonResult js = gson.fromJson(result, JsonResult.class);

        // 判断状态码是否为空
        if (js.getStatus() == null || js.getStatus().isEmpty()) {
            try {
                Thread.sleep(1000); // 等待1秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            js = gson.fromJson(result, JsonResult.class); // 再次尝试获取状态码和消息
        }


        if (js.getStatus().equals("100000")) {
            k=1;
        } if (js.getStatus().equals("100001")) {
            k=2;
        }

        System.out.printf("status:" + js.getStatus() + ",message:" + js.getMessage());
        System.out.println("network response: " + result);
        return k;
    }
}

package com.example.history.bean.Threads;

import android.os.AsyncTask;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        try {
            // 打开连接
            URL url = new URL("http://139.155.248.158:8081");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // 创建multipart/form-data请求，将文件作为流写入请求体中
            String boundary = "---------------------------" + System.currentTimeMillis();
            String lineEnd = "\r\n";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
            writer.append("--" + boundary).append(lineEnd);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + params[0] + "\"").append(lineEnd);
            writer.append("Content-Type: image/jpeg").append(lineEnd);
            writer.append(lineEnd);
            FileInputStream fileInputStream = new FileInputStream(params[0]);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            fileInputStream.close();
            writer.append(lineEnd);
            writer.append("--" + boundary + "--").append(lineEnd);
            writer.close();

            // 获取响应
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            Log.d("UploadTask", "Response code: " + responseCode);
            Log.d("UploadTask", "Response message: " + responseMessage);

            // 断开连接
            connection.disconnect();

            // 返回响应字符串
            return responseMessage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

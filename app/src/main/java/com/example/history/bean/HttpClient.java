package com.example.history.bean;

import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utils.JsonToObject;

public class HttpClient {
    public static void main(String[] args) throws IOException {
        System.out.println(""); // 输出空行
    }

    /*
     * 实现远程登录功能
     * @param username 用户名
     * @param password 密码
     * @return 返回服务器响应内容
     */
    public static String login(String username, String password) throws IOException{
        // 构造请求URL
        URL url = new URL("http://139.155.248.158:18080/history/LoginServlet?username="+username+"&password="+password);

        // 打开连接
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // 设置请求方法为GET
        con.setRequestMethod("GET");

        // 读取响应数据
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect(); // 断开连接
        Log.d("HttpClient","content----"+content.toString());
        return content.toString(); // 返回响应数据
    }
    public static String register(String username,String password,String nickname) throws IOException {
        URL url = new URL("http://139.155.248.158:18080/history/RegisterServlet");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // post方法构建请求参数
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write("username="+username+"&password="+password+"&nickname="+nickname);
        writer.flush();

        // 获取响应结果
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuffer content=new StringBuffer();
        while ((line = reader.readLine()) != null) {
            content.append(line);
            Log.d("TAG","line-------"+line);
        }
        reader.close();
        conn.disconnect();
        return content.toString();
    }


    public static List<DynastyContent> getDynastyContent(String option) throws Exception{
        URL url = new URL("http://139.155.248.158:18080/history/DynastyContentListServlet?option="+option);
        return queryContent(url,"GET",option);
    }

    public static List<DynastyContent> find(String keyword,String...option) throws IOException, JSONException {
        URL url = new URL("http://139.155.248.158:18080/history/SearchServlet?keyword="+keyword+"&option = "+option);
        return queryContent(url,"GET");
    }

    public static List<DynastyContent> getCollection() throws IOException, JSONException {
        URL url = new URL("http://139.155.248.158:18080/history/SearchServlet?");

        return queryContent(url,"GET");
    }

    public static void SendMsg(String username,String contentId,String recordType, String option) throws IOException {
        Log.d("SetRecord","接受的username"+username+",contentId:"+contentId+",recordType"+recordType);
        URL url = new URL("http://139.155.248.158:18080/history/SetRecord?username="+username+"&contentId="+contentId+"&recordType="+recordType+"&option="+option);
        Log.d("SetRecord",url.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        Log.d("SetRecord","执行完SendMsg");

        conn.getInputStream();
        conn.disconnect(); // 断开连接
    }

    public static List<DynastyContent> getRecord(String recordType,String username) throws IOException, JSONException {
        URL url = new URL("http://139.155.248.158:18080/history/GetRecord?recordType="+recordType+"&username="+username);
        return queryContent(url,"GET");
    }

    public static void clearHistory(String isClear) throws IOException {
        URL url = new URL("http://139.155.248.158:18080/history/ClearHistoryServlet?clear="+isClear);
        Log.d("ClearHistory","isClear:"+isClear);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        conn.getInputStream();
        conn.disconnect();
    }

    public static void changeNickname(String username,String nickname) throws IOException {
        URL url = new URL("http://139.155.248.158:18080/history/ChangeNicknameServlet?username="+username+"&nickname="+nickname);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        conn.getInputStream();
        conn.disconnect();
    }

    public static void changePassword(String username,String password) throws IOException {
        URL url = new URL("http://139.155.248.158:18080/history/ChangePassword?username="+username+"&password="+password);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        conn.getInputStream();
        conn.disconnect();
    }

    //第一个参数为访问的url，第二个是请求类型，第三个是需要的参数，
    public static List<DynastyContent> queryContent(URL url, String type, String... strings) throws IOException, JSONException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(type);

        //获取响应数据
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        String responseData = response.toString();
        Log.d("HistoryBrowse","queryContent数据："+responseData);
        in.close();
        List<DynastyContent> dynastyContentList=new ArrayList<>();
        if(responseData!=null||responseData.length()!=0||responseData!="[]"){
            List<Map<String,String>> maps = JsonToObject.parseArrayMap(responseData);
            for (Map<String,String> s:maps
            ) {
                DynastyContent dynastyContent=new DynastyContent(Integer.valueOf(s.get("id")),s.get("title"),s.get("content"),s.get("brief"),s.get("img"));
                dynastyContentList.add(dynastyContent);
            }
            conn.disconnect();
        }else{
            dynastyContentList.add(new DynastyContent(1,"NULL","NULL","NULL","NULL"));
        }
        Log.d("HistoryBrowse","返回数据："+dynastyContentList);
        return dynastyContentList;
    }
}

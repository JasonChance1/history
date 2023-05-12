package utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.history.bean.DynastyContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonToObject {

    public static Map<String,String> parseMap(String string) throws JSONException {
//        String result = "";
//        int startIndex = string.indexOf("[");
//        int endIndex = string.indexOf("]");
//
//
//        if (startIndex != -1 && endIndex != -1) {
//            result = string.substring(startIndex + 1, endIndex);

        Map<String, String> map = new HashMap<>();
       if(!TextUtils.isEmpty(string)){

           JSONObject jsonObject = new JSONObject(string);
           Iterator<String> keys = jsonObject.keys();
           while (keys.hasNext()) {
               String key = keys.next();
               String value = jsonObject.getString(key);
               map.put(key, value);
           }
       }
//        }else {
//            System.out.printf("获取数据异常");
//        }
        return map;
    }
    public static List<Map<String,String>> parseArrayMap(String string) throws JSONException {
        List<Map<String, String>> list = new ArrayList<>();
        JSONArray jsonArray=new JSONArray(string);
        for(int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            Map<String, String> map = new HashMap<>();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.getString(key);
                map.put(key, value);
            }
            list.add(map);
        }
        Log.d("Test","map<String,String>集合"+list);
        return list;
    }
    public static String sub(String string){
                String result = "";
        int startIndex = string.indexOf("[");
        int endIndex = string.indexOf("]");


        if (startIndex != -1 && endIndex != -1) {
            result = string.substring(startIndex + 1, endIndex);
    }
        return result;}
}

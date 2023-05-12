package utils;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class JSONParser {

    /**
     * 将JSON字符串转换为JSONObject对象
     * @param jsonString JSON格式的字符串
     * @return JSONObject对象
     */

    public static String extractString(String input) {
        String result = "";
        int startIndex = input.indexOf("[");
        int endIndex = input.indexOf("]");

        if (startIndex != -1 && endIndex != -1) {
            result = input.substring(startIndex + 1, endIndex);
        }
        return result;
    }


    public static JSONObject parseObject(String jsonString) throws JSONException {
//        String s =  extractString(jsonString);
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject(jsonString);
    }

    /**
     * 获取JSONObject中指定key对应的value
     * @param jsonObject JSONObject对象
     * @param key 键名
     * @return 对应的值
     */
    public static Object getObject(JSONObject jsonObject, String key) {
        try {
            return jsonObject.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 将JSON字符串转换为JSONArray对象
     * @param jsonString JSON格式的字符串
     * @return JSONArray对象
     */
    public static JSONArray parseArray(String jsonString) {
        try {
            return new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取JSONArray中指定下标处的元素
     * @param jsonArray JSONArray对象
     * @param index 下标
     * @return 指定下标处的元素
     */
    public static Object getObject(JSONArray jsonArray, int index) {
        try {
            return jsonArray.get(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}


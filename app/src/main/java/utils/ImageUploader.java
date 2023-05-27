package utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageUploader {

    private static final String TAG = "ImageUploader";
    private static final String SERVER_URL = "http://139.155.248.158:18080/history/UploadServlet";

    private Context context;

    public ImageUploader(Context context) {
        this.context = context;
    }

    public void uploadImage(String imagePath, String id) {
        File file = new File(imagePath);

        // 创建包含文件和id参数的请求体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", id)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse(getMimeType(file.getPath())), file))
                .build();

        // 创建请求
        Request request = new Request.Builder()
                .url(SERVER_URL)
                .post(requestBody)
                .build();

        // 创建客户端并发送请求
        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // 上传成功
                String responseData = response.body().string();
                Log.d(TAG, "上传成功。响应：" + responseData);
                // 在这里处理响应结果
            } else {
                // 上传失败
                Log.e(TAG, "上传失败。状态码：" + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "上传过程中发生 IOException");
        }
    }

    private String getMimeType(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    // 用于从内容URI获取图像的绝对路径的辅助方法
    private String getImagePathFromUri(Uri imageUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(imageUri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            return imagePath;
        }
        return null;
    }

    // 示例用法：
    public void exampleUsage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 启动一个选择图片的活动
        // 确保在 onActivityResult 方法中处理结果
        // 并调用 uploadImage() 方法传递所选图片路径和id参数。
    }
}
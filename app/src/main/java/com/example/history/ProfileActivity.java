package com.example.history;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.history.bean.Account;
import com.example.history.bean.MySqliteOpenHelper;
import com.example.history.bean.Threads.ChangeNicknameThread;
import com.example.history.bean.Threads.ChangePasswordThread;
import com.example.history.bean.Threads.UploadTask;
import com.example.history.bean.model.CurrentLogin;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.HttpUtil;
import utils.ImageUploader;
import utils.ToastUtil;
import utils.Utils;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private LinearLayout img_change, username_change;
    private TextView nickname_profile;
    private TextView password_change;
    private ImageView imageView;
    private MySqliteOpenHelper db;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static String path = Environment.getExternalStorageDirectory().getPath();//获取系统根目录
    public static String rootPath = path + "/历史通/";
    private String imgPath="1679298523686.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initData();
        initView();

        verifyStoragePermissions(ProfileActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (!Environment.isExternalStorageManager()){
                Uri uri = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                startActivity(intent);
            }
        }

        bindEvent();
        //修改头像
        img_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("修改头像");
                String[] items={"相册"};

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0://调用相册
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                                break;
                        }
                        AlertDialog dialog1 = builder.create();
                        dialog1.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });

    }


    //从相册或相机返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            imgPath = getRealPathFromUri(uri);
            Log.d("TAG","imgpaht:"+imgPath);
            int lastIndex = imgPath.lastIndexOf("/");
            String fileName1 = imgPath.substring(lastIndex + 1);//获取图片名.后缀
            CurrentLogin c = db.getCurrentUser();
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    ImageUploader imageUploader = new ImageUploader(ProfileActivity.this);
                    imageUploader.uploadImage(imgPath, c.getUid());
                }
            }.start();


//            db.setCurrentUser(c.getUsername(),c.getPassword(),c.getNickname(),fileName1,c.getUid());
//            new Thread(){
//                @Override
//                public void run() {
//                    try {
////                        uploadImage("http://139.155.248.158:18080/history/UploadServlet",imgPath);
////                        uploadImage("http://139.155.248.158:18080/history/UploadServlet",imgPath);
////                        Log.d("TAG","imgpaht:"+imgPath);
////                        uploadImage("http://139.155.248.158:18080/history/TestUpload",imgPath);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
        }

    }


    private void uploadImage(String url, String filePath) throws IOException {
        Log.e("------","上传图片filePath"+filePath);
        // 创建URL对象
        URL urlObj = new URL(url);
        // 打开连接
        HttpURLConnection conn = (HttpURLConnection)urlObj.openConnection();
        // 设置请求方法
        conn.setRequestMethod("POST");
        // 允许输入输出流
        conn.setDoInput(true);
        conn.setDoOutput(true);
        Log.e("------","允许输入流");
        String boundary = "****";
        // 设置请求头
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        Log.e("------","设置请求头");
        // 获取输出流
        OutputStream out = conn.getOutputStream();
        Log.e("------","获取输出流");
        // 缓存区大小
        byte[] buffer = new byte[4096];
        int len = 0;
        // 读取文件
        FileInputStream fis = new FileInputStream(new File(filePath));
        Log.e("------","读取文件fis");

        StringBuilder sb = new StringBuilder();
        sb.append("--" + boundary + "\r\n");
        sb.append("Content-Disposition: form-data; name=\"file\"; fileName=\"" + filePath + "\"\r\n");
        sb.append("Content-Type: application/octet-stream\r\n\r\n");

        sb.append("--" + boundary + "\r\n");
        sb.append("Content-Disposition: form-data; name=\"id\"\r\n\r\n");
        String id = db.getCurrentUser().getUid();
        CurrentLogin c = db.getCurrentUser();
        Log.e("ProfileActivity:","username"+c.getUsername()+",nickname"+c.getNickname()+",id:"+c.getUid());
        sb.append(1 + "\r\n");


        out.write(sb.toString().getBytes());
        Log.e("------","写入请求头,id:"+db.getCurrentUser().getUid()+","+sb);
        // 写入文件数据
        while ((len = fis.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        Log.e("------","上传图片"+out);
        // 写入请求尾
        String endStr = "\r\n--" + boundary + "--\r\n";
        out.write(endStr.getBytes());
        Log.e("------","写入请求尾"+endStr);
        // 关闭流
        fis.close();
        out.flush();
        out.close();
        // 获取服务器返回结果
        InputStream in = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String result = "";
        String line = "";
        while ((line = reader.readLine()) != null) {
            result += line;
        }
        Log.e("------","返回结果"+result);
        // 关闭流
        in.close();
    }

    public void verifyStoragePermissions(Activity activity) {
        // 检查是否已经授权
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 如果未授权，则弹出权限请求对话框
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            return null;
        }

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }

private void initData(){
    db = new MySqliteOpenHelper(ProfileActivity.this);
}
    private void initView(){
        img_change = findViewById(R.id.head_img_change_ll);
        username_change = findViewById(R.id.username_linearlayout);
        nickname_profile = findViewById(R.id.username_profile);
        password_change = findViewById(R.id.password_change_profile);
        imageView = findViewById(R.id.head_img_profile);//头像

        String nickname = db.getCurrentUser().getNickname();
        nickname_profile.setText(nickname);
    }
    private void bindEvent(){
        //修改密码
        password_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(ProfileActivity.this);
                View view3 = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.layout_dialog_password_change, null);
                EditText originalPassword = view3.findViewById(R.id.password_original_dialog);
                EditText newPassword = view3.findViewById(R.id.password_new_dialog);
                EditText confirmPassword = view3.findViewById(R.id.password_confirm_dialog);


                builder2.setView(view3).setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String originStr = originalPassword.getText().toString();
                        String newpassword = newPassword.getText().toString().trim();
                        String confirmpassword = confirmPassword.getText().toString().trim();
                        if(!originStr.equals(db.getCurrentUser().getPassword())){
                            ToastUtil.showMsg(ProfileActivity.this,"原密码错误");
                            Log.e("TAG","原密码："+originStr+",数据库密码："+db.getCurrentUser().getPassword());
                        }else if(!newpassword.equals(confirmpassword)){
                            ToastUtil.showMsg(ProfileActivity.this,"两次密码不一致");
                        }else if(TextUtils.isEmpty(newpassword)){
                            ToastUtil.showMsg(ProfileActivity.this,"密码不能为空");
                        }else if(originStr.equals(newpassword)){
                            ToastUtil.showMsg(ProfileActivity.this,"新密码与旧密码相同");
                        }
                        else{
                            new ChangePasswordThread(db.getCurrentUser().getUsername(),newpassword).start();
                            CurrentLogin c = db.getCurrentUser();
                            ToastUtil.showMsg(ProfileActivity.this,"修改成功");
//                            db.setCurrentUser(c.getUsername(),newpassword,c.getNickname(),c.getAvatar(),c.getUid());
                        }

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setCancelable(false).show();
            }
        });

        //更改昵称
        username_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileActivity.this);
                View view2 = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.layout_dialog_username_change, null);
                EditText nicknameChange = view2.findViewById(R.id.et_nickname_change);
                builder1.setView(view2).setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newNickname = nicknameChange.getText().toString();
                        nickname_profile.setText(newNickname);
                        ChangeNicknameThread thread = new ChangeNicknameThread(db.getCurrentUser().getUsername(),newNickname);
                        thread.start();
                        db.setCurrentUser(db.getCurrentUser().getUsername(),db.getCurrentUser().getPassword(),newNickname,db.getCurrentUser().getAvatar(),db.getCurrentUser().getUid());
                        ToastUtil.showMsg(ProfileActivity.this,"修改成功");
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });
    }

}
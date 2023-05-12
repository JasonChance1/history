package com.example.history;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.history.bean.Account;
import com.example.history.bean.MySqliteOpenHelper;
import com.example.history.bean.Threads.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.HttpUtil;
import utils.ToastUtil;
import utils.Utils;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private LinearLayout img_change, username_change;
    private TextView nickname_profile;
    private TextView password_change;
    private ImageView imageView;

    private String username, password, nickname, imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        img_change = findViewById(R.id.head_img_change_ll);
        username_change = findViewById(R.id.username_linearlayout);
        nickname_profile = findViewById(R.id.username_profile);
        password_change = findViewById(R.id.password_change_profile);
        imageView = findViewById(R.id.head_img_profile);//头像

        //获取用户名，通过用户名找到
        Intent intent = getIntent();
        nickname = intent.getExtras().getString("nickname");
        nickname_profile.setText(nickname);

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
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
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

        password_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(ProfileActivity.this);
                View view3 = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.layout_dialog_password_change, null);
                EditText original_password = view3.findViewById(R.id.password_original_dialog);
                EditText new_password = view3.findViewById(R.id.password_new_dialog);
                EditText confirm_password = view3.findViewById(R.id.password_confirm_dialog);

                builder2.setView(view3).setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
                EditText new_username = view2.findViewById(R.id.et_username_change);//输入框中的用户名
                builder1.setView(view2).setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Account account = new Account();
                        account.setNickname(new_username.getText().toString());
                        account.setUsername(username);
                        account.setPassword(password);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });

        if (imagePath != null) {
            Uri uri = Uri.parse(imagePath);
            imageView.setImageURI(uri); //将ImageView的图像设置为该Uri指向的图像
        } else { //否则，使用默认背景图像
            imageView.setImageResource(R.drawable.default_avatar);
        }
    }

    //从相册或相机返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

        }

    }

}
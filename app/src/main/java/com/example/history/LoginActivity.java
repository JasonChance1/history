package com.example.history;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.history.bean.MySqliteOpenHelper;
import com.example.history.bean.Threads.NetWorkThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


import utils.JsonToObject;
import utils.ToastUtil;
import android.os.Build;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn,registerBtn,resetpasswordBtn;
    private EditText username,password;
    private CheckBox rememberPassword,autoLogin;
    private MySqliteOpenHelper db;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int SETTINGS_REQUEST_CODE = 101;

    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            // Add more permissions if needed
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new MySqliteOpenHelper(LoginActivity.this);
        setContentView(R.layout.activity_login);
        // 检查权限
//        if (checkPermissions()) {
//            // 所有权限已授予
//            performStartupOperations();
//        } else {
//            // 请求权限
//            requestPermissions();
//        }

        //找到相应控件并设置相应的点击事件
        initViewAndSetOnClick();

        rememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    autoLogin.setChecked(false);
                }
            }
        });
        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rememberPassword.setChecked(true);
                }
            }
        });

    }

    public class OnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent=null;
            switch (v.getId()){
                case R.id.btn_login:
                    try {
                        checkPassword(v);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.btn_for_register:
                    intent=new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_for_resetpassword:
                    intent=new Intent(LoginActivity.this,ResetPasswordActivity.class);
                    startActivity(intent);
                    break;
            }

        }
    }
    //判断账号密码是否正确
    public boolean checkPassword(View view) throws ExecutionException, InterruptedException, JSONException {
        String name=username.getText().toString().trim();//输入的用户名
        String password1=password.getText().toString().trim();//输入的密码

        NetWorkThread netWorkThread=new NetWorkThread(name,password1);
        FutureTask futureTask=new FutureTask(netWorkThread);
        Thread thread=new Thread(futureTask);
        thread.start();
        String content = (String) futureTask.get();
        Log.d("LoginActivity",content);
        Map<String,String> map = JsonToObject.parseMap(content);
        String status = map.get("status");
        String data=map.get("data");
        Map<String,String> dataMap=JsonToObject.parseMap(JsonToObject.sub(data));
        String nickname=dataMap.get("nickname");
        String avatar = dataMap.get("avatar");
        String uid = dataMap.get("id");

        System.out.printf("avatar-------"+avatar);
        if(TextUtils.equals(status,"100000")){
            ToastUtil.showMsg(LoginActivity.this,"登录成功");
            if(TextUtils.isEmpty(db.getCurrentUser().getUsername())){
                db.setCurrentUser(name,password1,nickname,avatar.substring(2),uid);
            }else{
                db.clearTable();
                db.setCurrentUser(name,password1,nickname,avatar.substring(2),uid);
            }

            if(rememberPassword.isChecked()){
                SharedPreferences sharedPreferences=getSharedPreferences("spfPassword",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("username",name);
                editor.putString("password",password1);
                editor.putBoolean("isRemember",true);
                Log.e("MainActivity","nickname"+nickname);
                if(autoLogin.isChecked()){
                    editor.putBoolean("isAutoLogin",true);
                }else{
                    editor.putBoolean("isAutoLogin",false);
                }
                editor.apply();
            }else{
                SharedPreferences sharedPreferences=getSharedPreferences("spfPassword",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("isRemember",false);
                editor.apply();
            }
            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);

//            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
        } if(TextUtils.equals(status,"100001")){
            ToastUtil.showMsg(LoginActivity.this,"密码错误");
        } if(TextUtils.equals(status,"100002")){
            ToastUtil.showMsg(LoginActivity.this,"账号不存在");
        }else{
            ToastUtil.showMsg(LoginActivity.this,"点击"+status);
            System.out.printf("map--------"+String.valueOf(map));
            Log.d("","map--------"+String.valueOf(map));
        }

        return false;
    }

    public void initViewAndSetOnClick(){
        //登录按钮，注册账号按钮，忘记密码按钮
        loginBtn=findViewById(R.id.btn_login);
        registerBtn=findViewById(R.id.btn_for_register);
        resetpasswordBtn=findViewById(R.id.btn_for_resetpassword);
        rememberPassword=findViewById(R.id.remember_password_checkbox);
        autoLogin=findViewById(R.id.auto_login_checkbox);
        username=findViewById(R.id.et_username_login);
        password=findViewById(R.id.et_password_login);

        OnClick onClick=new OnClick();
        passwordIsChecked();
        loginBtn.setOnClickListener(onClick);
        registerBtn.setOnClickListener(onClick);
        resetpasswordBtn.setOnClickListener(onClick);

    }

    private void passwordIsChecked(){
        SharedPreferences spf=getSharedPreferences("spfPassword",MODE_PRIVATE);
        //默认没有勾选
        boolean isRemember = spf.getBoolean("isRemember",false);
        boolean isAutoLogin = spf.getBoolean("isAutoLogin",false);
        //默认账号密码为空
        String usernameRemember=spf.getString("username","");
        String passwordRemember=spf.getString("password","");
        if(isAutoLogin){
            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("username",usernameRemember);
            intent.putExtras(bundle);
            startActivity(intent);
            LoginActivity.this.finish();
        }
        if(isRemember){
            username.setText(usernameRemember);
            password.setText(passwordRemember);
            rememberPassword.setChecked(true);
        }
    }


//    private boolean checkPermissions() {
//        // 检查每个权限是否已授予
//        for (String permission : permissions) {
//            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private void requestPermissions() {
//        // 构建待请求的权限列表
//        List<String> pendingPermissions = new ArrayList<>();
//        for (String permission : permissions) {
//            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                pendingPermissions.add(permission);
//            }
//        }
//
//        // 发起权限请求
//        ActivityCompat.requestPermissions(this, pendingPermissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
//    }
//
//    private void performStartupOperations() {
//        // 在这里执行应用程序的启动操作
//        // ...
//
//        // 进入应用程序的主界面
//        startActivity(new Intent(this, HomeActivity.class));
//        finish();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            boolean allPermissionsGranted = true;
//
//            // 检查授权结果
//            for (int grantResult : grantResults) {
//                if (grantResult != PackageManager.PERMISSION_GRANTED) {
//                    allPermissionsGranted = false;
//                    break;
//                }
//            }
//
//            if (allPermissionsGranted) {
//                // 所有权限已授予
//                performStartupOperations();
//            } else {
//                // 某些权限未被授予
//                showPermissionDeniedMessage();
//            }
//        }
//    }
//
//    private void showPermissionDeniedMessage() {
//        // 显示权限被拒绝的提示
//        // ...
//
//        // 打开应用程序设置页面，以便用户手动授予权限
//        openAppSettings();
//    }
//
//    private void openAppSettings() {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(Uri.parse("package:" + getPackageName()));
//        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == SETTINGS_REQUEST_CODE) {
//            // 检查权限
//            if (checkPermissions()) {
//                // 用户已在设置页面授予了所有权限
//                performStartupOperations();
//            } else {
//                // 用户仍然未授予所有权限
//                showPermissionDeniedMessage();
//            }
//        }
//    }
}
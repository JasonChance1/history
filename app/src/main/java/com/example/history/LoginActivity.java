package com.example.history;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


import utils.JsonToObject;
import utils.ToastUtil;
import android.os.Build;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn,registerBtn,resetpasswordBtn;
    private EditText username,password;
    private MySqliteOpenHelper mySqliteOpenHelper;
    private CheckBox rememberPassword,autoLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //找到相应控件并设置相应的点击事件
        initViewAndSetOnClick();

        mySqliteOpenHelper=new MySqliteOpenHelper(this);

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

        if(TextUtils.equals(status,"100000")){
            ToastUtil.showMsg(LoginActivity.this,"登录成功");
            if(rememberPassword.isChecked()){
                SharedPreferences sharedPreferences=getSharedPreferences("spfPassword",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("username",name);
                editor.putString("password",password1);
                editor.putBoolean("isRemember",true);
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
            Bundle bundle=new Bundle();
            Log.d("MineFragment","传过去的nickname:"+nickname);
            bundle.putString("nickname",nickname);
            bundle.putString("username",username.getText().toString());
            bundle.putString("transition","fade");
            intent.putExtras(bundle);
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


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (!Environment.isExternalStorageManager()){
//                Uri uri = Uri.parse("package:" + getPackageName());
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
//                startActivity(intent);
//            }
//        }




}
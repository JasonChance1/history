package com.example.history;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.history.bean.MySqliteOpenHelper;
import com.example.history.bean.Threads.RegisterCallable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import utils.ToastUtil;

public class RegisterActivity extends AppCompatActivity {
    private Button register;
    private EditText username,password,password_confirm,nickname;
    private MySqliteOpenHelper mySqliteOpenHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        register=findViewById(R.id.register_btn);
        username=findViewById(R.id.username_register);
        nickname=findViewById(R.id.nickname_register);
        password=findViewById(R.id.password_register);
        password_confirm=findViewById(R.id.password_register_confirm);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取输入框的账号、密码、用户名等信息
                String username_input=username.getText().toString().trim();
                String password_input=password.getText().toString().trim();
                String password_confirm_input=password_confirm.getText().toString().trim();
                String nickname_input=nickname.getText().toString().trim();

                if(TextUtils.isEmpty(password_input) ||TextUtils.isEmpty(username_input)){
                    ToastUtil.showMsg(RegisterActivity.this,"账号或密码不能为空");
                }
                else if(!password_input.equals(password_confirm_input)){
                    ToastUtil.showMsg(RegisterActivity.this,"两次输入的密码不一致");
                }
                else{
                    String s = null;
                    try {
                        s = register(username_input,password_input,nickname_input);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(s.equals("失败")){
                        ToastUtil.showMsg(RegisterActivity.this,"该账号已注册");
                    }else if(s.equals("注册成功")){
                        Intent intent=new Intent(RegisterActivity.this,HomeActivity.class);
                        ToastUtil.showMsg(RegisterActivity.this,"注册成功");
                        startActivity(intent);
                    }
                }
//                System.out.printf("test" +String.valueOf(new NetworkTask().execute()));
//                    ToastUtil.showMsg(RegisterActivity.this, String.valueOf(new NetworkTask().execute()));

            }
        });
        mySqliteOpenHelper=new MySqliteOpenHelper(this);
    }

//    public void insert(View view){
//        Account account=new Account();
//
//        account.setUsername(username.getText().toString().trim());
//        account.setPassword(password.getText().toString().trim());
//        account.setNickname(nickname.getText().toString().trim());
//
//
//        long i=mySqliteOpenHelper.insertAccount(account);
//        if (i==-1) ToastUtil.showMsg(RegisterActivity.this,"该账号已注册");
//        else{
//            Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
//            startActivity(intent);
//            ToastUtil.showMsg(RegisterActivity.this,"注册成功");
//        }
//
//    }

    public String register(String username,String password,String nickname) throws ExecutionException, InterruptedException {
//        final String[] result = new String[1];
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//
//                    result[0] = HttpClient.register(username,password,nickname);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//        return null;
        RegisterCallable registerCallable=new RegisterCallable(username,password,nickname);
        FutureTask futureTask=new FutureTask(registerCallable);
        new Thread(futureTask).start();
        String result= (String) futureTask.get();

        return result;
    }

}
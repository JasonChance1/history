package com.example.history;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.history.bean.Account;
import com.example.history.bean.MySqliteOpenHelper;
import utils.ToastUtil;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText username,passwordOriginal,passwordNew;
    private Button resetConfirm;
    private MySqliteOpenHelper mySqliteOpenHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        username=findViewById(R.id.username_reset);
        passwordOriginal=findViewById(R.id.password_original);
        passwordNew=findViewById(R.id.password_new);
        resetConfirm=findViewById(R.id.password_reset_btn);

        resetConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update(view);
            }
        });
        mySqliteOpenHelper=new MySqliteOpenHelper(this);
    }
    public void update(View view){
        String username1=username.getText().toString().trim();//要更改密码的用户
        String passwordOriginal1=passwordOriginal.getText().toString().trim();//输入框中输入的原密码
        String newpassword=passwordNew.getText().toString().trim();//新密码
        String password_db=mySqliteOpenHelper.queryPasswordByNameFromAccount(username1);//数据库中获取的原密码
        String nickname=mySqliteOpenHelper.queryNickNameByNameFromAccount(username1);//通过输入的用户名查询昵称

       /* 修改条件
        1.原始密码输入正确
        2.新密码与旧密码不能相同
        3.新密码不能为空*/
        if(passwordOriginal1.equals(password_db) && !newpassword.equals(passwordOriginal1)&& !TextUtils.isEmpty(newpassword)){
            Account account=new Account();
            account.setUsername(username1);
            account.setPassword(newpassword);
            account.setNickname(nickname);


            int i=mySqliteOpenHelper.updateDataAccount(account);
            if(i>0){
                ToastUtil.showMsg(ResetPasswordActivity.this,"修改成功");
            }else{
                ToastUtil.showMsg(ResetPasswordActivity.this,"该账号尚未注册");
            }
        }else if(passwordOriginal1.equals(newpassword)){
            ToastUtil.showMsg(ResetPasswordActivity.this,"新密码与旧密码不能相同");
        }
        else if(TextUtils.isEmpty(newpassword)){
            ToastUtil.showMsg(ResetPasswordActivity.this,"新密码不能为空");
        }
        else{
            ToastUtil.showMsg(ResetPasswordActivity.this,"请确认输入的密码是否正确");
        }

    }

//    public void querry(View view){
//        String username2=username.getText().toString().trim();
//
//        List<Account> accounts=mySqliteOpenHelper.querryFromAccountByName(username2);
//        String res="";
//        for (Account a:accounts
//             ) {
//           res+=""+a.getUsername()+" "+a.getPassword();
//        }
//    }

}
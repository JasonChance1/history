package com.example.history;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.history.bean.DynastyContent;
import com.example.history.bean.MySqliteOpenHelper;
import com.mysql.fabric.xmlrpc.base.Data;

public class DetailChineseHistoryActivity extends AppCompatActivity {
    private TextView content,shareContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_chinese_history);

        initView();
        initData();
        bindEvent();
    }

    private void initData(){
        Intent intent=getIntent();

        String s =intent.getStringExtra("data");
        Log.d("DetailChinese",s);
        content.setText(s);
    }

    private void initView(){
        content=findViewById(R.id.detail_content_tv);
        shareContent = findViewById(R.id.share_content);
    }

    private void bindEvent(){
        shareContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(() -> {
                    allShare("我的分享"+content.getText().toString()); // 不传递文件参数
                }).start();
            }
        });
    }


    public void allShare(String share) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, share);
        shareIntent = Intent.createChooser(shareIntent, share);
        startActivity(shareIntent);
    }
}
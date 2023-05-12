package com.example.history;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.history.bean.DynastyContent;
import com.example.history.bean.MySqliteOpenHelper;
import com.mysql.fabric.xmlrpc.base.Data;

public class DetailChineseHistoryActivity extends AppCompatActivity {
    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_chinese_history);

        content=findViewById(R.id.detail_content_tv);

        Intent intent=getIntent();

        String s =intent.getStringExtra("data");
        Log.d("DetailChinese",s);
        content.setText(s);


    }
}
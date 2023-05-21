package com.example.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.history.bean.DynastyContent;
import com.example.history.bean.MySqliteOpenHelper;
import com.example.history.bean.Threads.GetRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import listview.DynastyListViewAdapter;

public class MyCollectionsActivity extends AppCompatActivity {
    private ListView lv;
    private String username;
    private List<DynastyContent> list = new ArrayList<>();
    private MySqliteOpenHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collections);
        db = new MySqliteOpenHelper(MyCollectionsActivity.this);


        lv = findViewById(R.id.listview);
        username = db.getCurrentUser().getUsername();
        GetRecord getRecord = new GetRecord("0",username);
        FutureTask futureTask = new FutureTask(getRecord);
        Thread thread = new Thread(futureTask);
        thread.start();
        try {
            list = (List<DynastyContent>) futureTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DynastyListViewAdapter adapter = new DynastyListViewAdapter(MyCollectionsActivity.this,list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DynastyContent dynastyContent = list.get(position);
                Intent intent = new Intent(MyCollectionsActivity.this,DetailChineseHistoryActivity.class);
                intent.putExtra("data",dynastyContent.getContent());
                startActivity(intent);
            }
        });
    }
}
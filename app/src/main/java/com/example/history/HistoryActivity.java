package com.example.history;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.history.bean.DynastyContent;
import com.example.history.bean.MySqliteOpenHelper;
import com.example.history.bean.Threads.ClearHistory;
import com.example.history.bean.Threads.GetRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import listview.DynastyListViewAdapter;
import utils.ToastUtil;

public class HistoryActivity extends AppCompatActivity {
    private ListView listView;
    private Button clear;
    private List<DynastyContent> dc = new ArrayList<>();
    private MySqliteOpenHelper db;
    private DynastyListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = new MySqliteOpenHelper(HistoryActivity.this);
        String username = db.getCurrentUser().getUsername();
        listView = findViewById(R.id.listview);
        GetRecord getRecord = new GetRecord("1",username,"1");
        FutureTask futureTask = new FutureTask(getRecord);
        Thread thread=new Thread(futureTask);
        thread.start();

        GetRecord getRecord1 = new GetRecord("1",username,"2");
        FutureTask futureTask1 = new FutureTask(getRecord1);
        Thread thread1=new Thread(futureTask1);
        thread1.start();
        try {
            dc = (List<DynastyContent>) futureTask.get();
            dc.addAll((List<DynastyContent>)futureTask1.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter = new DynastyListViewAdapter(HistoryActivity.this,dc);
        listView.setAdapter(adapter);

        clear = findViewById(R.id.clear_history);
        clear.setOnClickListener(v -> {
            ClearHistory clearHistory = new ClearHistory("1",username);
            clearHistory.start();
            ToastUtil.showMsg(HistoryActivity.this,"清除历史");

        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            DynastyContent dynastyContent = dc.get(position);
            Intent intent = new Intent(HistoryActivity.this,DetailChineseHistoryActivity.class);
            intent.putExtra("data",dynastyContent.getContent());
            startActivity(intent);
        });
    }

}
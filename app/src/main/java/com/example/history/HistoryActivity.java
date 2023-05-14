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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = new MySqliteOpenHelper(HistoryActivity.this);
        Log.d("HistoryBrowse","HistoryActivity");
        String username = db.getCurrentUser().getUsername();
        listView = findViewById(R.id.listview);
        GetRecord getRecord = new GetRecord("1",username);
        FutureTask futureTask = new FutureTask(getRecord);
        Thread thread=new Thread(futureTask);

        thread.start();
        try {
            dc = (List<DynastyContent>) futureTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.d("HistoryBrowse","异常1："+dc);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("HistoryBrowse","异常2："+dc);
        }
        Log.d("HistoryBrowse","HistoryActivity接收数据："+dc);
        DynastyListViewAdapter adapter = new DynastyListViewAdapter(HistoryActivity.this,dc);
        listView.setAdapter(adapter);

        clear = findViewById(R.id.clear_history);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearHistory clearHistory = new ClearHistory("1");
                clearHistory.start();
                ToastUtil.showMsg(HistoryActivity.this,"清除历史");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DynastyContent dynastyContent = dc.get(position);
                Intent intent = new Intent(HistoryActivity.this,DetailChineseHistoryActivity.class);
                intent.putExtra("data",dynastyContent.getContent());
                startActivity(intent);
            }
        });
    }
}
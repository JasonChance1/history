package com.example.history.bean.Threads;

import android.util.Log;

import com.example.history.bean.DynastyContent;
import com.example.history.bean.HttpClient;

import java.util.List;
import java.util.concurrent.Callable;

public class GetRecord implements Callable<List<DynastyContent>> {
    String recordType;
    String username;
    String opt;
    public GetRecord(String recordType,String username,String opt){
        this.username = username;
        this.recordType = recordType;
        this.opt = opt;
    }
    @Override
    public List<DynastyContent> call() throws Exception {
        Log.d("HistoryBrowse","调用线程");
        return HttpClient.getRecord(recordType,username,opt);
    }
}

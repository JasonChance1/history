package com.example.history.bean.Threads;

import android.util.Log;

import com.example.history.bean.DynastyContent;
import com.example.history.bean.HttpClient;

import java.util.List;
import java.util.concurrent.Callable;

public class SearchCallable implements Callable<List<DynastyContent>> {
    private String keyword;
    public SearchCallable(String keyword){
        this.keyword = keyword;
    }
    @Override
    public List<DynastyContent> call() throws Exception {
        List<DynastyContent> list = HttpClient.find(keyword);
        Log.d("Search","线程中搜索的结果"+list.toString());
        return list;
    }
}

package com.example.history.bean.Threads;

import android.util.Log;

import com.example.history.bean.DynastyContent;
import com.example.history.bean.HttpClient;

import java.util.List;
import java.util.concurrent.Callable;

public class GetDCCallable implements Callable<List<DynastyContent>> {
    private String option;
    public GetDCCallable(String option){
        this.option = option;
    }
    @Override
    public List<DynastyContent> call() throws Exception {
       List<DynastyContent> dynastyContentList = HttpClient.getDynastyContent(option);
        Log.d("TestGetDCCable",dynastyContentList.toString());
       return dynastyContentList;
    }
}

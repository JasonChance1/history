package com.example.history.bean.Threads;

import com.example.history.bean.DynastyContent;
import com.example.history.bean.HttpClient;

import java.util.List;
import java.util.concurrent.Callable;

public class GetRecommendCallable implements Callable<List<DynastyContent>> {
    @Override
    public List<DynastyContent> call() throws Exception {
        return HttpClient.getRecommend();
    }
}

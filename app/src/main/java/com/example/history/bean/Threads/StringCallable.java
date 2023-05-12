package com.example.history.bean.Threads;

import android.util.Log;

import com.example.history.bean.Account;
import com.example.history.bean.JdbcTools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class StringCallable implements Callable<List<Account>> {
    public static String sql;
    public StringCallable(String sql){
        this.sql=sql;
    }
    @Override
    public List<Account> call() throws Exception {
        Log.d("MineFragment","StringCallable:sql语句为："+sql);
        List<Account> accounts=JdbcTools.query(sql,Account.class);
        Log.d("MineFragment","StringCallable:accounts结果为："+accounts);
        return accounts;
    }
}

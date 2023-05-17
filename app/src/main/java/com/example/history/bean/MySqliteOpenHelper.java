package com.example.history.bean;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.history.bean.model.CurrentLogin;
import com.example.history.bean.model.LocalInfo;

import java.util.ArrayList;
import java.util.List;

public class MySqliteOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME="MySql.db";
    public static final String TABLE_NAME_CURRENT_LOGIN="current_login";
    public static final String TABLE_USER_LOCAL = "localInfo";



    public MySqliteOpenHelper(Context context){
        super(context,DB_NAME,null,3);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+TABLE_NAME_CURRENT_LOGIN+" (username text,password text, nickname text,avatar text,id text)");
        sqLiteDatabase.execSQL("create table "+ TABLE_USER_LOCAL + "(username text primary key, imgcover text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion > newVersion){
            return;
        }
        db.execSQL("drop table if exists "+TABLE_NAME_CURRENT_LOGIN);
        db.execSQL("drop table if exists "+TABLE_USER_LOCAL);
        onCreate(db);
    }

    public int updateDataAccount(Account account){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();

        return 0;
    }


    public String queryNickNameByNameFromAccount(String username){
        String s="null";

        return s;
    }

    public String queryPasswordByNameFromAccount(String username){
        String s="null";
        SQLiteDatabase db=getWritableDatabase();
        return s;
    }
    public long setCurrentUser(String username,String password,String nickname,String avatar,String id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username",username);
        cv.put("password",password);
        cv.put("nickname",nickname);
        cv.put("avatar",avatar);
        cv.put("id",id);
        return db.insert(TABLE_NAME_CURRENT_LOGIN,null,cv);

    }

    public CurrentLogin getCurrentUser(){
        SQLiteDatabase db = getWritableDatabase();
        CurrentLogin currentLogin = new CurrentLogin();
        Cursor cursor = db.query(TABLE_NAME_CURRENT_LOGIN,null,null,null,null,null,null,null);
        if(cursor!=null){
            while(cursor.moveToNext()){
                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("username"));
                @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password"));
                @SuppressLint("Range") String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
                @SuppressLint("Range") String avatar = cursor.getString(cursor.getColumnIndex("avatar"));
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
                currentLogin.setNickname(nickname);
                currentLogin.setPassword(password);
                currentLogin.setUsername(username);
                currentLogin.setAvatar(avatar);
                currentLogin.setUid(id);
                Log.e("lOGIN123","-----------USERNAME,"+username+",PASSWORD:"+password+"id"+id+"AVATAR:"+avatar);
            }
        }
        return currentLogin;
    }

    public void clearTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_CURRENT_LOGIN,null,null);
    }

    public void saveOrUpdate(LocalInfo localInfo) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("username", localInfo.getUsername());
        cv.put("imgcover", localInfo.getImgcover());

        String[] whereArgs = {localInfo.getUsername()};

        int rowsAffected = db.update(TABLE_USER_LOCAL, cv, "username = ?", whereArgs);
        if (rowsAffected == 0) {
            db.insert(TABLE_USER_LOCAL, null, cv);
        }
    }



    @SuppressLint("Range")
    public LocalInfo getLocalInfoByUsername(String username) {
        SQLiteDatabase db = getWritableDatabase();
        LocalInfo localInfo = new LocalInfo();
        Cursor cursor = db.query(TABLE_USER_LOCAL, null, "username = ?", new String[]{username}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            localInfo.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            localInfo.setImgcover(cursor.getString(cursor.getColumnIndex("imgcover")));
        }
        cursor.close();
        return localInfo;
    }


}


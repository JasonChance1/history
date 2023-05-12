package com.example.history.bean;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MySqliteOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME="MySql.db";
    public static final String TABLE_NAME_ACCOUNT="account";
    public static final String TABLE_NAME_COLLECTION="collection";
    public static final String TABLE_NAME_CONTENT="content";



    public static final String COLUMN_URI="uri";
    public static final String COLUMN_USERNAME="username";
    public static final String COLUMN_NICKNAME="nickname";
    public static final String COLUMN_PASSWORD="password";
    public static final String COLUMN_CONTENT="content";
    public static final String COLUMN_TITLE="title";
    public static final String COLUMN_DETAIL="detail";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_ID_COLLECTION = "id_collection";
    private static final String COLUMN_ID = "id_dc";

    public MySqliteOpenHelper(Context context){
        super(context,DB_NAME,null,3);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建数据表
        String sql= "CREATE TABLE " + TABLE_NAME_ACCOUNT + "(" +
                COLUMN_USERNAME + " varchar(50) PRIMARY KEY, " +
                COLUMN_NICKNAME + " varchar(50) NOT NULL, " +
                COLUMN_PASSWORD + " varchar(50)," +
                COLUMN_URI + " varchar(50))";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.execSQL("create table "+ TABLE_NAME_COLLECTION+ "(_" +
                COLUMN_ID_COLLECTION+" INTEGER PRIMARY KEY,"+
                COLUMN_TITLE +" TEXT, " +
                COLUMN_CONTENT+" TEXT," +
                COLUMN_URL+" TEXT,"+
                COLUMN_DETAIL+" TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME_CONTENT + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_URL + " VARCHAR(50),"+
                COLUMN_DETAIL+" TEXT )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion > newVersion){
            return;
        }
        db.execSQL("drop table if exists "+TABLE_NAME_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CONTENT);
        onCreate(db);
    }

    public long insertAccount(Account account){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(COLUMN_USERNAME,account.getUsername());
        values.put(COLUMN_PASSWORD,account.getPassword());
        values.put(COLUMN_NICKNAME,account.getNickname());
        values.put(COLUMN_URI,account.getAvatar());

        return db.insert(TABLE_NAME_ACCOUNT,null,values);
    }


    public int updateDataAccount(Account account){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(COLUMN_USERNAME,account.getUsername());
        values.put(COLUMN_PASSWORD,account.getPassword());
        values.put(COLUMN_NICKNAME,account.getNickname());
        values.put(COLUMN_URI,account.getAvatar());
//        values.put("bitmap",account.getBitmap());

        return db.update(TABLE_NAME_ACCOUNT,values,"username like ?",new String[] {account.getUsername()});//将username为account.getUsername()的更改为values
    }


    public String queryNickNameByNameFromAccount(String username){
        String s="null";
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME_ACCOUNT,null,"username like ?",new String[]{username},
                null,null,null);
        if(cursor!=null){
            while(cursor.moveToNext()){
                s=cursor.getString(cursor.getColumnIndexOrThrow("nickname"));
            }
        }
        return s;
    }

    public String queryPasswordByNameFromAccount(String username){
        String s="null";
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME_ACCOUNT,null,"username like ?",new String[]{username},
                null,null,null);
        if(cursor!=null){
            while(cursor.moveToNext()){
                s=cursor.getString(cursor.getColumnIndexOrThrow("password"));
            }
        }
        return s;
    }

    @SuppressLint("Range")
    public void UpdateUrlByName(String username,String url){
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_URI,url);
        SQLiteDatabase db=getWritableDatabase();
        db.update(TABLE_NAME_ACCOUNT, cv,
                COLUMN_USERNAME + "=?",
                new String[] { username });

    }

    @SuppressLint("Range")
    public String queryImageByNameFromAccount(String username) {
        String uri = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT uri FROM account WHERE username = ?", new String[] {username});
        if (cursor.moveToFirst()) {
            // 如果有结果，则获取第一行的URI值
            uri = cursor.getString(cursor.getColumnIndex(COLUMN_URI));
        }
        cursor.close();
        return uri;
    }

    public DynastyContent getContentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("content", null, COLUMN_ID+"=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
            @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex(COLUMN_URL));
            @SuppressLint("Range") String detail = cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL));
            DynastyContent dy = new DynastyContent(id, content, url, title, detail);
            cursor.close();
            return dy;
        }
        return null;
    }
//
//    public long insertContentToCollection(DynastyContent content) {
//        if (content == null) {
//            Log.e("dd","dc为空");
//            return -1; // 或者抛出 IllegalArgumentException 异常
//
//        }
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("_"+COLUMN_ID_COLLECTION, content.getId());
//        values.put(COLUMN_TITLE, content.getTitle());
//        values.put(COLUMN_CONTENT, content.getContent());
//        values.put(COLUMN_URL, content.getUrl());
//        values.put(COLUMN_DETAIL,content.getDetailContent());
//        return db.insert(TABLE_NAME_COLLECTION, null, values);
//    }
    // 获取记录总数
    public int getContentCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_CONTENT, null);
        int count = 0;

        try {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }

        return count;
    }

    // 查询所有历史内容的记录
    @SuppressLint("Range")
    public List<DynastyContent> getAllContents() {
        List<DynastyContent> contentList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_CONTENT, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
//            DynastyContent content = new DynastyContent();
//            content.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
//            content.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
//            content.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
//            content.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
//            content.setDetailContent(cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL)));
//            contentList.add(content);
        }

        cursor.close();
        return contentList;
    }
//    // 插入记录
//    public long insertContent(DynastyContent content) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_ID, content.getId());
//        values.put(COLUMN_TITLE, content.getTitle());
//        values.put(COLUMN_CONTENT, content.getContent());
//        values.put(COLUMN_URL, content.getUrl());
//        values.put(COLUMN_DETAIL,content.getDetailContent());
//        return db.insert(TABLE_NAME_CONTENT, null, values);
//    }

//    // 根据ID更新记录
//    public int updateContent(DynastyContent dynastyContent) {
//        SQLiteDatabase db = getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_TITLE, dynastyContent.getTitle());
//        values.put(COLUMN_CONTENT, dynastyContent.getContent());
//        values.put(COLUMN_URL, dynastyContent.getUrl());
//        values.put(COLUMN_DETAIL,dynastyContent.getDetailContent());
//
//        String whereClause = COLUMN_ID + "=?";
//        String[] whereArgs = {String.valueOf(dynastyContent.getId())};
//
//        return db.update(TABLE_NAME_CONTENT, values, whereClause, whereArgs);
//    }

}

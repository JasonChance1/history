package com.example.history.bean;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.history.R;
import com.example.history.bean.Threads.GetRecord;
import com.example.history.bean.adapter.RecyclerViewAdapter;
import com.example.history.bean.model.CurrentLogin;
import com.example.history.bean.model.LocalInfo;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ProfileDrawerActivity extends AppCompatActivity {
    private ImageView imgCover;
    private final int PICK_IMAGE_REQUEST = 1;

    private RecyclerView collectionRv,historyRv;
    private MySqliteOpenHelper db;

    private RecyclerViewAdapter collectionAdapter,historyAdapter;

    private List<DynastyContent> collectionList;
    private List<DynastyContent> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_drawer);

        try {
            initData();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        initView();
    }

    private void initData() throws ExecutionException, InterruptedException {
        db = new MySqliteOpenHelper(ProfileDrawerActivity.this);
        CurrentLogin user = db.getCurrentUser();
        GetRecord getRecord = new GetRecord("1",user.getUsername());
        FutureTask futureTask = new FutureTask(getRecord);
        Thread thread=new Thread(futureTask);
        thread.start();
        historyList = (List<DynastyContent>) futureTask.get();



        GetRecord getRecord1 = new GetRecord("0",user.getUsername());
        FutureTask futureTask1 = new FutureTask(getRecord1);
        Thread thread1 = new Thread(futureTask1);
        thread1.start();
        collectionList = (List<DynastyContent>) futureTask1.get();
    }
    private void initView(){
        imgCover = findViewById(R.id.img_cover);
        String username = db.getCurrentUser().getUsername();
        String url = db.getLocalInfoByUsername(username).getImgcover();
        if (TextUtils.isEmpty(url)) {
            imgCover.setImageResource(R.drawable.cover_default);
        } else {
            imgCover.setImageURI(Uri.fromFile(new File(url)));

        }

        imgCover.setOnClickListener(v -> openGallery());
        collectionRv = findViewById(R.id.collection_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        collectionRv.setLayoutManager(layoutManager);
        collectionAdapter = new RecyclerViewAdapter(collectionList,ProfileDrawerActivity.this);
        collectionRv.setAdapter(collectionAdapter);

        historyRv = findViewById(R.id.history_recyclerview);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        historyRv.setLayoutManager(layoutManager1);
        historyAdapter = new RecyclerViewAdapter(historyList,ProfileDrawerActivity.this);
        historyRv.setAdapter(historyAdapter);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();
            String imagePath = getRealPathFromURI(imageUri);

            // 将数据库中保存的图片URL更新为真实路径
            CurrentLogin c = db.getCurrentUser();
            LocalInfo localInfo = new LocalInfo(c.getUsername(), imagePath);
            db.saveOrUpdate(localInfo);

            // 加载图片
            if (!TextUtils.isEmpty(imagePath)) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Uri imageContentUri = FileProvider.getUriForFile(
                            this,
                            getApplicationContext().getPackageName() + ".fileprovider",
                            imageFile
                    );
                    imgCover.setImageURI(imageContentUri);
                } else {
                    // 图片文件不存在
                    imgCover.setImageResource(R.drawable.cover_default);
                }
            } else {
                // 图片路径为空
                imgCover.setImageResource(R.drawable.cover_default);
            }
        }
    }


    public String getRealPathFromURI(Uri uri) {
        String filePath = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                filePath = cursor.getString(columnIndex);
                cursor.close();
            }
        }
        return filePath;
    }
}

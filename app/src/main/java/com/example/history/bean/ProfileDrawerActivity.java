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
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.SmartGlideImageLoader;

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
        GetRecord getHistory = new GetRecord("1",user.getUsername(),"1");
        FutureTask gethistoryFutureTask = new FutureTask(getHistory);
        Thread historyThread=new Thread(gethistoryFutureTask);
        historyThread.start();
        GetRecord getHistory1 = new GetRecord("1",user.getUsername(),"2");
        FutureTask gethistoryFutureTask1 = new FutureTask(getHistory1);
        Thread historyThread1=new Thread(gethistoryFutureTask1);
        historyThread1.start();
        historyList = (List<DynastyContent>) gethistoryFutureTask.get();
        historyList.addAll ((List<DynastyContent>) gethistoryFutureTask1.get());

        GetRecord getRecord = new GetRecord("0",user.getUsername(),"1");
        FutureTask futureTask = new FutureTask(getRecord);
        Thread thread = new Thread(futureTask);
        thread.start();

        GetRecord getRecord1 = new GetRecord("0",user.getUsername(),"2");
        FutureTask futureTask1 = new FutureTask(getRecord1);
        Thread thread1 = new Thread(futureTask1);
        thread1.start();
        collectionList = (List<DynastyContent>) futureTask.get();
        collectionList.addAll((List<DynastyContent>)futureTask1.get());
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

        imgCover.setOnClickListener(v -> {
            new XPopup.Builder(ProfileDrawerActivity.this).asBottomList("", new String[]{"修改封面", "查看图片"},
                            (position, text) -> {
                                if(position==0){
                                    openGallery();
                                }
                                else if(position==1){
                                    new XPopup.Builder(ProfileDrawerActivity.this)
                                            .asImageViewer(imgCover, "http://139.155.248.158:18080/history/uploads/1685094517837_%E7%A7%A6.png", new SmartGlideImageLoader())
                                            .show();
                                }
                            })
                    .show();
        });
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

package com.example.history.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.history.R;
import com.example.history.bean.adapter.BookDownloadAdapter;
import com.example.history.bean.model.Item;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import utils.ToastUtil;

public class BookDownLoadActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookDownloadAdapter adapter;
    private List<Item> itemList;
    private Handler handler;

    private Button importBook;
    private Runnable progressRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_download);

      initData();
      initView();
      bindEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除更新进度的Runnable，防止内存泄漏
        if (handler != null && progressRunnable != null) {
            handler.removeCallbacks(progressRunnable);
        }

    }

    private void initData(){
        // 初始化数据
        itemList = new ArrayList<>();
        itemList.add(new Item("南明史"));
        itemList.add(new Item("中国近代史（徐中约）"));
        itemList.add(new Item("中国通史（上卷）（图说天下系列）"));
        itemList.add(new Item("中国通史（下卷）（图说天下系列）"));
        itemList.add(new Item("哈佛中国史"));

    }


    private void initView(){

        importBook = findViewById(R.id.import_book);

        // 初始化 RecyclerView 和适配器
        recyclerView = findViewById(R.id.bookdown_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookDownloadAdapter(this, itemList);
        recyclerView.setAdapter(adapter);
    }
    private void bindEvent(){
        importBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new XPopup.Builder(BookDownLoadActivity.this).asCenterList("请选择类型", new String[]{"PDF文件", ".txt","awz3",".epub",".mobi"}, new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        if(position==0){
                            openFileChooser();
                        }else{
                            ToastUtil.showMsg(BookDownLoadActivity.this,"暂时不支持该格式");
                        }
                    }
                }).show();
            }
        });


        // 设置 RecyclerView 的下载按钮点击监听器
        adapter.setDownloadButtonClickListener(new BookDownloadAdapter.DownloadButtonClickListener() {
            @Override
            public void onDownloadButtonClick(int position, BookDownloadAdapter.DownloadProgressListener progressListener) {
                // 处理下载按钮点击事件，根据 position 获取相应的数据项或执行其他操作
                Item clickedItem = itemList.get(position);
                // ...处理下载按钮点击事件的逻辑

                // 更新进度条的进度
                handler = new Handler();
                progressRunnable = new Runnable() {
                    int progress = 0;

                    @Override
                    public void run() {
                        progress += 5;
                        progressListener.onProgressUpdated(progress);

                        if (progress >= 100) {
                            Intent intent = new Intent(BookDownLoadActivity.this, BookReadActivity.class);
                            startActivity(intent);
                        } else {
                            handler.postDelayed(this, 50); // 每隔50毫秒更新一次进度条
                        }
                    }
                };
                handler.postDelayed(progressRunnable, 50); // 延迟50毫秒开始更新进度
            }
        });
        adapter.setItemClickListener(new BookDownloadAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ToastUtil.showMsg(BookDownLoadActivity.this,"位置："+position);
                Intent intent = new Intent(BookDownLoadActivity.this,BookDetailActivity.class);
                startActivity(intent);
            }
        });
    }


    private static final int REQUEST_CODE_FILE_CHOOSER = 1;

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FILE_CHOOSER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                // 将所选文件复制到应用程序的存储目录中
                copyFileToAppStorage(uri);
            }
        }
    }

    private void copyFileToAppStorage(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);

            File appStorageDir = getFilesDir(); // 获取应用程序的存储目录
            File outputFile = new File(appStorageDir, "imported.pdf"); // 创建输出文件

            OutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            // 文件复制完成，可以进行后续处理

        } catch (IOException e) {
            e.printStackTrace();
            // 复制文件时出现错误，进行错误处理
        }
    }

}
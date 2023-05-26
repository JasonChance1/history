package com.example.history;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.example.history.bean.DynastyContent;
import com.example.history.bean.MySqliteOpenHelper;
import com.mysql.fabric.xmlrpc.base.Data;

public class DetailChineseHistoryActivity extends AppCompatActivity {
    private TextView shareContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_chinese_history);

        initView();
        initData();
        bindEvent();
    }

    private void initData(){
        Intent intent=getIntent();

        String s =intent.getStringExtra("data");
        Log.d("DetailChinese",s);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            content.setText(Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY));
//        } else {
//            content.setText(Html.fromHtml(s));
//        }
        WebView webView = findViewById(R.id.webView);
        webView.loadDataWithBaseURL(null, s, "text/html", "UTF-8", null);

    }

    private void initView(){
        shareContent = findViewById(R.id.share_content);
    }

    private void bindEvent(){
        shareContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(() -> {
                    allShare(); // 不传递文件参数
                }).start();
            }
        });
    }

  public void allShare() {
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            WebView webView = findViewById(R.id.webView);
            webView.evaluateJavascript("(function() { return document.documentElement.innerHTML; })();",
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            String htmlContent = value.replaceAll("^\"|\"$", ""); // 移除返回结果中的引号

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, htmlContent);
                            shareIntent = Intent.createChooser(shareIntent, "分享");
                            startActivity(shareIntent);
                        }
                    });
        }
    });
}


}
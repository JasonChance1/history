package com.example.history.bean;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.example.history.R;
import com.example.history.bean.model.CurrentLogin;
import com.example.history.bean.model.LocalInfo;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.SmartGlideImageLoader;

import utils.ToastUtil;
import utils.Utils;

public class ProfileDrawerActivity extends AppCompatActivity {
    private ImageView imgCover;
    private final int PICK_IMAGE_REQUEST = 1;

    private MySqliteOpenHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_drawer);

        initView();
        bindEvent();
    }

    private void initView(){
        db = new MySqliteOpenHelper(ProfileDrawerActivity.this);
        imgCover = findViewById(R.id.img_cover);

        LocalInfo localInfo = db.getLocalInfoByUsername(db.getCurrentUser().getUsername());
        if(!TextUtils.isEmpty(localInfo.getImgcover())){
            Uri uri = Uri.parse(localInfo.getImgcover());
            imgCover.setImageURI(uri);
        }
    }

    private void bindEvent(){
        imgCover.setOnClickListener(v -> {
            new XPopup.Builder(ProfileDrawerActivity.this)
                    .asBottomList("", new String[]{"查看图片", "修改封面"},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    if(position==0){
                                        new XPopup.Builder(ProfileDrawerActivity.this)
                                                .asImageViewer(imgCover, db.getLocalInfoByUsername(db.getCurrentUser().getUsername()).getImgcover(), new SmartGlideImageLoader())
                                                .show();

                                    }
                                    if(position==1){
                                        openGallery();
                                    }
                                }
                            })
                    .show();
        });
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
            imgCover.setImageURI(imageUri);

            // 获取图片的URL
            String imageUrl = Utils.getRealPath(ProfileDrawerActivity.this,data);
            // 在这里处理你需要的图片URL
            // ...

            CurrentLogin c = db.getCurrentUser();

            LocalInfo localInfo = new LocalInfo(c.getUsername(),imageUrl);

            db.insertLocalInfo(localInfo);
        }
    }

}
package com.example.history;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.history.bean.DynastyContent;
import com.example.history.bean.MySqliteOpenHelper;

import java.io.IOException;
import java.io.OutputStream;

public class TestActivity extends AppCompatActivity {
    private EditText contentEditText, titleEditText,detailEditText;
    private ImageView imageView;
    private Button addButton;
    private MySqliteOpenHelper db;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        contentEditText = findViewById(R.id.test_content);
        titleEditText = findViewById(R.id.test_title);
        imageView = findViewById(R.id.test_img);
        addButton = findViewById(R.id.test_add);
        detailEditText=findViewById(R.id.detail_content_et);
        db=new MySqliteOpenHelper(this);

        //点击图片调用相册
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 3);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获取输入框的内容
                String content = contentEditText.getText().toString().trim();
                String title = titleEditText.getText().toString().trim();
                String detail=detailEditText.getText().toString().trim();

                //判断输入是否合法
                if (content.isEmpty() || title.isEmpty()) {
                    Toast.makeText(TestActivity.this, "内容和标题不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (imageUri == null) {
                    Toast.makeText(TestActivity.this, "请选择一张图片", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {

                    //从相册中选取图片并保存
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imageUri = saveImage(bitmap); // 保存图片到应用程序私有目录中，并获取新的Uri

                    DynastyContent dynastyContent = new DynastyContent();

//                    //将输入框的数据存入到数据库
//                    dynastyContent.setTitle(title);
//                    dynastyContent.setContent(content);
//                    dynastyContent.setDetailContent(detail);
//                    dynastyContent.setUrl(imageUri.toString()); // 使用私有目录中保存的Uri
//                    int count=db.getContentCount();
//                    dynastyContent.setId(count);
//                    db.insertContent(dynastyContent);

                    Toast.makeText(TestActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(TestActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private Uri saveImage(Bitmap bitmap) throws IOException {
        String filename = "image_" + System.currentTimeMillis() + ".jpg";
        OutputStream out = null;
        try {
            out = openFileOutput(filename, MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return Uri.fromFile(getFileStreamPath(filename));
    }
}

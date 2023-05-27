package fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.history.HistoryActivity;
import com.example.history.MyCollectionsActivity;
import com.example.history.ProfileActivity;
import com.example.history.R;

import com.example.history.SettingActivity;
import com.example.history.bean.Account;
import com.example.history.bean.JdbcTools;
import com.example.history.bean.MySqliteOpenHelper;
import com.example.history.bean.Threads.StringCallable;
import com.example.history.bean.model.CurrentLogin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MineFragment extends Fragment {
    private LinearLayout profile_ll;
    private TextView nickname;
    private ImageView imageView;
    private Button setting,collections,history;
    private MySqliteOpenHelper db;
    public MineFragment(){

    }
    @SuppressLint("SuspiciousIndentation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_mine, container, false);
        db = new MySqliteOpenHelper(getActivity());
        initView(view);
        if(!TextUtils.isEmpty(db.getCurrentUser().getAvatar()))
            Glide.with(getActivity()).load("http://139.155.248.158:18080/history/"+db.getCurrentUser().getAvatar()).into(imageView);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        CurrentLogin currentLogin = db.getCurrentUser();

        nickname.setText(currentLogin.getNickname());
        Log.e("MainActivity","当前用户："+currentLogin.getUsername()+",密码："+currentLogin.getPassword()+"昵称："+currentLogin.getNickname());
        //点击头像框进入个人界面
        profile_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        //进入设置界面
        setting.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(), SettingActivity.class);
            startActivity(intent);
        });

        //进入收藏界面
        collections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), MyCollectionsActivity.class);
                startActivity(intent);
            }
        });

        //浏览记录
        history.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HistoryActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        nickname.setText(db.getCurrentUser().getNickname());
        Glide.with(getActivity()).load("http://139.155.248.158:18080/history/"+db.getCurrentUser().getAvatar()).into(imageView);

    }

    private void initView(View view){
        profile_ll=view.findViewById(R.id.profile_ll);
        nickname=view.findViewById(R.id.nickname_mine_fragment);
        imageView=view.findViewById(R.id.img_touxiang);
        setting=view.findViewById(R.id.setting_btn);
        collections=view.findViewById(R.id.collections_btn_mind);
        history = view.findViewById(R.id.browse_record);
        imageView.setImageResource(R.drawable.default_avatar);
    }


}
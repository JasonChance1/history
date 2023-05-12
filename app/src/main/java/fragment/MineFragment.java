package fragment;

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

import com.example.history.HistoryActivity;
import com.example.history.MyCollectionsActivity;
import com.example.history.ProfileActivity;
import com.example.history.R;

import com.example.history.SettingActivity;
import com.example.history.bean.Account;
import com.example.history.bean.JdbcTools;
import com.example.history.bean.MySqliteOpenHelper;
import com.example.history.bean.Threads.StringCallable;

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
    private String mParam1;
    private String mParam2;

    private ImageView imageView;
    private Button setting,collections,history;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String nickname1="默认昵称";
    private String username = "";
    public MineFragment(String data){
        this.mParam1=data;
    }
    public MineFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_mine, container, false);
        nickname1=getArguments().getString("nickname1");
        username=getArguments().getString("username");
        Log.d("MineFragment","nickname:"+nickname1+",username"+username);
        return view;
    }

    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        profile_ll=view.findViewById(R.id.profile_ll);
        nickname=view.findViewById(R.id.nickname_mine_fragment);
        imageView=view.findViewById(R.id.img_touxiang);
        setting=view.findViewById(R.id.setting_btn);
        collections=view.findViewById(R.id.collections_btn_mind);
        history = view.findViewById(R.id.browse_record);

        imageView.setImageResource(R.drawable.default_avatar);
        nickname.setText(nickname1);

        //点击头像框进入个人界面
        profile_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ProfileActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("nickname",nickname1);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //进入设置界面
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        //进入收藏界面
        collections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), MyCollectionsActivity.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });

        //浏览记录
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });

    }
}
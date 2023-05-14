package com.example.history;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.transition.Slide;
import android.util.DisplayMetrics;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.history.bean.MySqliteOpenHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import fragment.ChineseHistoryFragment;
import fragment.CommonSenseFragment;
import fragment.MineFragment;
import fragment.WorldHistoryFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
public class HomeActivity extends AppCompatActivity {
    private ChineseHistoryFragment chineseHistoryFragment;
    private CommonSenseFragment commonSenseFragment;
    private MineFragment mineFragment;
    private WorldHistoryFragment worldHistoryFragment;
    private RadioButton chinese,world,commonsense,mine;
    private FloatingActionButton drawerBtn;
    private DrawerLayout drawerLayout;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        username = getIntent().getStringExtra("username");

        initAndSetClick();

        chineseHistoryFragment=new ChineseHistoryFragment();
        Bundle bundle2=new Bundle();
        bundle2.putString("username1",username);
        chineseHistoryFragment.setArguments(bundle2);
        getSupportFragmentManager().beginTransaction().add(R.id.container_fragment,chineseHistoryFragment).commitAllowingStateLoss();
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // 侧边栏打开时隐藏按钮
                drawerBtn.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // 侧边栏关闭时显示按钮
                drawerBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });
    }

    class OnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.chinese_fragment:

                    if(chineseHistoryFragment==null) chineseHistoryFragment=new ChineseHistoryFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,chineseHistoryFragment).commitAllowingStateLoss();
                    break;

                case R.id.world_fragment:
                    if(worldHistoryFragment==null) worldHistoryFragment=new WorldHistoryFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,worldHistoryFragment).commitAllowingStateLoss();
                    break;
                case R.id.commonsense_fragment:
                    if(commonSenseFragment==null) commonSenseFragment=new CommonSenseFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,commonSenseFragment).commitAllowingStateLoss();
                    break;
                case R.id.mine_fragment:
                    if(mineFragment==null) mineFragment=new MineFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,mineFragment).commitAllowingStateLoss();
                    break;
                case R.id.drawerBtn:
                    drawerLayout.open();
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mineFragment != null) {
//            mineFragment.onFragmentResume();
//        }
    }

    private void initAndSetClick(){
        chinese=findViewById(R.id.chinese_fragment);
        world=findViewById(R.id.world_fragment);
        commonsense=findViewById(R.id.commonsense_fragment);
        mine=findViewById(R.id.mine_fragment);
        drawerBtn=findViewById(R.id.drawerBtn);
        drawerLayout=findViewById(R.id.drawerLayer);

        OnClick onClick=new OnClick();

        chinese.setOnClickListener(onClick);
        world.setOnClickListener(onClick);
        commonsense.setOnClickListener(onClick);
        mine.setOnClickListener(onClick);
//        drawerBtn.setOnClickListener(onClick);
        drawerBtn.setOnTouchListener(new View.OnTouchListener() {

            private int lastX, lastY;
            private boolean isDragging = false;


            public int getScreenHeight() {
                return screenHeight;
            }

            public int getScreenWidth() {
                return screenWidth;
            }

            int screenWidth = getScreenWidth();
            int screenHeight = getScreenHeight();
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        isDragging = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        if (!isDragging && (dx != 0 || dy != 0)) {
                            isDragging = true;
                        }
                        if (isDragging) {
                            v.setX(v.getX() + dx);
                            v.setY(v.getY() + dy);
                            lastX = (int) event.getRawX();
                            lastY = (int) event.getRawY();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isDragging) {
                            drawerLayout.open();
                            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                drawerLayout.closeDrawer(GravityCompat.START);
                            } else {
                                drawerLayout.openDrawer(GravityCompat.START);
                            }
                            Toast.makeText(HomeActivity.this, "Button clicked", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        });
    }
}
package com.example.guetapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.guetapp.adapter.MainViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private MainViewPagerAdapter adapter;
    
    /**
     * 导航到指定页面
     * @param pageIndex 页面索引：0-主页, 1-视频, 2-聊天, 3-我的
     */
    public void navigateToPage(int pageIndex) {
        if (viewPager != null && pageIndex >= 0 && pageIndex < 4) {
            viewPager.setCurrentItem(pageIndex, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initDatum();
        initListeners();
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    /**
     * 初始化数据
     */
    private void initDatum() {
        adapter = new MainViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        // 禁用ViewPager的滑动切换，只能通过底部导航栏切换
        viewPager.setUserInputEnabled(false);
    }

    /**
     * 初始化监听器
     */
    private void initListeners() {
        // 底部导航栏点击事件
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                viewPager.setCurrentItem(0, false);
                return true;
            } else if (itemId == R.id.nav_video) {
                viewPager.setCurrentItem(1, false);
                return true;
            } else if (itemId == R.id.nav_chat) {
                viewPager.setCurrentItem(2, false);
                return true;
            } else if (itemId == R.id.nav_me) {
                viewPager.setCurrentItem(3, false);
                return true;
            }
            return false;
        });

        // ViewPager页面切换监听
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 同步更新底部导航栏选中状态
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_video);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_chat);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.nav_me);
                        break;
                }
            }
        });
    }
}
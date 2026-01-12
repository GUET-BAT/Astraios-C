package com.example.guetapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.guetapp.fragment.ChatFragment;
import com.example.guetapp.fragment.HomeFragment;
import com.example.guetapp.fragment.MeFragment;
import com.example.guetapp.fragment.VideoFragment;

/**
 * 主页面ViewPager适配器
 * 管理4个Fragment：主页、视频、聊天、我的
 */
public class MainViewPagerAdapter extends FragmentStateAdapter {

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // 主页 - Flutter页面
                return HomeFragment.newInstance();
            case 1:
                // 视频页 - 原生页面
                return new VideoFragment();
            case 2:
                // 聊天页 - 原生页面
                return new ChatFragment();
            case 3:
                // 我的页 - Flutter页面
                return MeFragment.newInstance();
            default:
                return HomeFragment.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}


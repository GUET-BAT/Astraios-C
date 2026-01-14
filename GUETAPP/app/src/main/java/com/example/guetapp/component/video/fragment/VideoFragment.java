package com.example.guetapp.component.video.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guetapp.R;
import com.example.guetapp.fragment.BaseFragment;

/**
 * 视频页面Fragment（原生实现）
 */
public class VideoFragment extends BaseFragment {

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, android.os.Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    protected void initViews() {
        TextView textView = findViewById(R.id.tv_video);
        if (textView != null) {
            textView.setText("视频页面");
        }
    }

    @Override
    protected void initDatum() {
        // 初始化数据
    }

    @Override
    protected void initListeners() {
        // 设置监听器
    }
}


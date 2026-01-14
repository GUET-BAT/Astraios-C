package com.example.guetapp.component.social.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guetapp.R;
import com.example.guetapp.fragment.BaseFragment;

/**
 * 聊天页面Fragment（原生实现）
 */
public class ChatFragment extends BaseFragment {

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, android.os.Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    protected void initViews() {
        TextView textView = findViewById(R.id.tv_chat);
        if (textView != null) {
            textView.setText("聊天页面");
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


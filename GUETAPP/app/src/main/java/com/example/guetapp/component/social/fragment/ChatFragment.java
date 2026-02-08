package com.example.guetapp.component.social.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guetapp.R;
import com.example.guetapp.fragment.BaseFragment;
import com.example.guetapp.manager.SessionManager;

/**
 * 聊天页面Fragment（原生实现）
 */
public class ChatFragment extends BaseFragment {

    private TextView tvUserName;

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
        
        tvUserName = findViewById(R.id.tv_user_name);
        updateUserName();
    }

    @Override
    protected void initDatum() {
        // 初始化数据
    }

    @Override
    protected void initListeners() {
        // 设置监听器
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次页面显示时更新用户名（处理登录/登出后的状态变化）
        updateUserName();
    }

    /**
     * 更新显示的用户名/游客状态
     */
    private void updateUserName() {
        if (tvUserName == null || getContext() == null) {
            return;
        }
        
        boolean isGuest = SessionManager.isGuest(getContext());
        String userName = SessionManager.getUserName(getContext());
        
        if (isGuest || userName == null || userName.isEmpty()) {
            tvUserName.setText("游客");
        } else {
            tvUserName.setText(userName);
        }
    }
}


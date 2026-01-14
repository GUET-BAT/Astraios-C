package com.example.guetapp.component.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.guetapp.MainActivity;
import com.example.guetapp.R;
import com.example.guetapp.activity.BaseActivity;
import com.example.guetapp.manager.SessionManager;

/**
 * 手机号登录页（占位，只实现跳转逻辑）
 */
public class PhoneLoginActivity extends BaseActivity {

    private Button btnBack;
    private Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
    }

    @Override
    protected void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnDone = findViewById(R.id.btn_done);
    }

    @Override
    protected void initListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnDone.setOnClickListener(v -> {
            // 模拟手机号登录成功
            SessionManager.setLoggedIn(this, true);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}



package com.example.guetapp.component.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.guetapp.MainActivity;
import com.example.guetapp.R;
import com.example.guetapp.activity.BaseActivity;
import com.example.guetapp.manager.SessionManager;

/**
 * QQ登录页（占位：不做具体功能，仅实现跳转逻辑）
 */
public class QQLoginActivity extends BaseActivity {

    private Button btnBack;
    private Button btnSimulateLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq_login);
    }

    @Override
    protected void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSimulateLogin = findViewById(R.id.btn_simulate_login);
    }

    @Override
    protected void initListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSimulateLogin.setOnClickListener(v -> {
            // 模拟第三方登录成功
            SessionManager.setLoggedIn(this, true);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}



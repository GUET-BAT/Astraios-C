package com.example.guetapp.component.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.guetapp.R;
import com.example.guetapp.activity.BaseActivity;
import com.example.guetapp.manager.SessionManager;

/**
 * 注册页：账号/手机号 + 密码 + 注册按钮
 * 注册成功后返回登录页（真实注册逻辑后续再接入）
 */
public class RegisterActivity extends BaseActivity {

    private Button btnBack;
    private Button btnRegister;
    private EditText etAccount;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnRegister = findViewById(R.id.btn_register_submit);
        etAccount = findViewById(R.id.et_register_account);
        etPassword = findViewById(R.id.et_register_password);
    }

    @Override
    protected void initListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String account = etAccount.getText() == null ? null : etAccount.getText().toString().trim();
            String password = etPassword.getText() == null ? null : etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "请输入账号/手机号和密码", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO：以后在这里接入真实“注册成功/失败”的判断逻辑
            boolean registerSuccess = true;

            if (registerSuccess) {
                // 注册成功后回到登录页，不自动登录
                SessionManager.logout(this);
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}



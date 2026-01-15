package com.example.guetapp.component.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.guetapp.MainActivity;
import com.example.guetapp.R;
import com.example.guetapp.activity.BaseActivity;
import com.example.guetapp.manager.SessionManager;

/**
 * 登录页（账号密码登录 + 各入口跳转）
 */
public class LoginActivity extends BaseActivity {

    private EditText etAccount;
    private EditText etPassword;

    private Button btnAccountLogin;
    private Button btnPhoneLogin;
    private Button btnRegister;
    private Button btnQqLogin;
    private Button btnWechatLogin;
    private Button btnGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initViews() {
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);

        btnAccountLogin = findViewById(R.id.btn_account_login);
        btnPhoneLogin = findViewById(R.id.btn_phone_login);
        btnRegister = findViewById(R.id.btn_register);
        btnQqLogin = findViewById(R.id.btn_qq_login);
        btnWechatLogin = findViewById(R.id.btn_wechat_login);
        btnGuest = findViewById(R.id.btn_guest);
    }

    @Override
    protected void initListeners() {
        btnAccountLogin.setOnClickListener(v -> {
            String account = etAccount.getText() == null ? null : etAccount.getText().toString().trim();
            String password = etPassword.getText() == null ? null : etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
                return;
            }

            // 这里不做真实鉴权，仅模拟“登录成功”
            SessionManager.setLoggedIn(this, true);
            goMainAndClearBackStack();
        });

        btnPhoneLogin.setOnClickListener(v ->
                startActivity(new Intent(this, PhoneLoginActivity.class))
        );

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        btnQqLogin.setOnClickListener(v ->
                startActivity(new Intent(this, QQLoginActivity.class))
        );

        btnWechatLogin.setOnClickListener(v ->
                startActivity(new Intent(this, WeChatLoginActivity.class))
        );

        btnGuest.setOnClickListener(v -> {
            SessionManager.setGuest(this, true);
            goMainAndClearBackStack();
        });
    }

    private void goMainAndClearBackStack() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}



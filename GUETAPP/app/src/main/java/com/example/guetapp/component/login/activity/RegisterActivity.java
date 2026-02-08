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

import java.util.HashMap;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodChannel;

/**
 * 注册页：账号/手机号 + 密码 + 注册按钮
 * 通过Flutter发起网络请求进行注册
 */
public class RegisterActivity extends BaseActivity {

    private Button btnBack;
    private Button btnRegister;
    private EditText etAccount;
    private EditText etPassword;

    // Flutter临时Engine用于调用Flutter注册
    private FlutterEngine registerFlutterEngine;
    private MethodChannel registerChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initFlutterEngineForRegister();
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

            // 调用Flutter侧的注册逻辑
            doFlutterRegister(account, password);
        });
    }

    /**
     * 初始化用于注册调用的FlutterEngine和MethodChannel
     */
    private void initFlutterEngineForRegister() {
        registerFlutterEngine = new FlutterEngine(getApplicationContext());
        // 执行默认dart入口
        registerFlutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );
        registerChannel = new MethodChannel(registerFlutterEngine.getDartExecutor().getBinaryMessenger(),
                "com.example.guetapp/native");
    }

    /**
     * 调用Flutter侧的注册逻辑
     */
    private void doFlutterRegister(String userName, String passWord) {
        if (registerChannel == null) {
            Toast.makeText(this, "Flutter通道未初始化", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, Object> args = new HashMap<>();
        args.put("userName", userName);
        args.put("passWord", passWord);
        registerChannel.invokeMethod("flutterRegister", args, new MethodChannel.Result() {
            @Override
            public void success(Object result) {
                // Flutter返回bool值，true表示注册成功
                boolean registerSuccess = result instanceof Boolean ? (Boolean) result : false;
                
                if (registerSuccess) {
                    // 注册成功后回到登录页，不自动登录
                    SessionManager.logout(RegisterActivity.this);
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void error(String errorCode, String errorMessage, Object errorDetails) {
                Toast.makeText(RegisterActivity.this, "注册异常: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void notImplemented() {
                Toast.makeText(RegisterActivity.this, "Flutter未实现注册方法", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (registerFlutterEngine != null) {
            registerFlutterEngine.destroy();
            registerFlutterEngine = null;
        }
        super.onDestroy();
    }
}



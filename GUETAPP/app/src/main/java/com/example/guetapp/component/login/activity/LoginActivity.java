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
import com.example.guetapp.plugin.MethodChannelHandler;

import java.util.HashMap;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodChannel;

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

    // Flutter临时Engine用于调用Flutter登录
    private FlutterEngine loginFlutterEngine;
    private MethodChannel loginChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initFlutterEngineForLogin();
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

            doFlutterLogin(account, password);
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

    /**
     * 初始化用于登录调用的FlutterEngine和MethodChannel
     */
    private void initFlutterEngineForLogin() {
        loginFlutterEngine = new FlutterEngine(getApplicationContext());
        // 执行默认dart入口
        loginFlutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );
        loginChannel = new MethodChannel(loginFlutterEngine.getDartExecutor().getBinaryMessenger(),
                "com.example.guetapp/native");
    }

    /**
     * 调用Flutter侧的登录逻辑
     */
    private void doFlutterLogin(String userName, String passWord) {
        if (loginChannel == null) {
            Toast.makeText(this, "Flutter通道未初始化", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, Object> args = new HashMap<>();
        args.put("userName", userName);
        args.put("passWord", passWord);
        loginChannel.invokeMethod("flutterLogin", args, new MethodChannel.Result() {
            @Override
            public void success(Object result) {
                if (result instanceof HashMap) {
                    HashMap<?, ?> map = (HashMap<?, ?>) result;
                    Object statusObj = map.get("status");
                    int status = statusObj instanceof Number ? ((Number) statusObj).intValue() : 0;
                    if (status == 200) {
                        String access = String.valueOf(map.get("accessToken"));
                        String refresh = String.valueOf(map.get("refreshToken"));
                        String userName = String.valueOf(map.get("userName"));
                        SessionManager.setTokens(LoginActivity.this, access, refresh, userName);
                        goMainAndClearBackStack();
                        return;
                    }
                    Toast.makeText(LoginActivity.this, "登录失败:" + map.get("msg"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void error(String errorCode, String errorMessage, Object errorDetails) {
                Toast.makeText(LoginActivity.this, "登录异常:" + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void notImplemented() {
                Toast.makeText(LoginActivity.this, "Flutter未实现登录方法", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (loginFlutterEngine != null) {
            loginFlutterEngine.destroy();
            loginFlutterEngine = null;
        }
        super.onDestroy();
    }
}



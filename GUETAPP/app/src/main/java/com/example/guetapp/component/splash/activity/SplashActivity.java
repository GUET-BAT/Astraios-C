package com.example.guetapp.component.splash.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.guetapp.MainActivity;
import com.example.guetapp.R;
import com.example.guetapp.activity.BaseActivity;
import com.example.guetapp.component.login.activity.LoginActivity;
import com.example.guetapp.manager.SessionManager;

/**
 * 启动页Activity
 * 显示启动画面，延迟后跳转到主页面
 */
public class SplashActivity extends BaseActivity {
    
    // 启动页显示时长（毫秒）
    private static final int SPLASH_DELAY = 2000; // 2秒
    
    private Handler handler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        handler = new Handler(Looper.getMainLooper());
        
        // 延迟跳转到主页面
        handler.postDelayed(() -> {
            navigateNext();
        }, SPLASH_DELAY);
    }
    
    /**
     * 根据登录/游客状态决定跳转
     */
    private void navigateNext() {
        Class<?> target = SessionManager.canEnterHome(this) ? MainActivity.class : LoginActivity.class;
        Intent intent = new Intent(SplashActivity.this, target);
        // 清栈：避免返回到启动页
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理Handler，避免内存泄漏
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
    
    @Override
    protected void initViews() {
        // 启动页不需要额外的视图初始化
    }
    
    @Override
    protected void initDatum() {
        // 可以在这里做一些初始化工作，比如检查更新、加载配置等
    }
    
    @Override
    protected void initListeners() {
        // 启动页不需要额外的监听器
    }
}

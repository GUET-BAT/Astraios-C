package com.example.guetapp.plugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * MethodChannel处理器
 * 处理Flutter通过MethodChannel调用原生Android功能
 */
public class MethodChannelHandler implements MethodChannel.MethodCallHandler {
    
    private static final String CHANNEL_NAME = "com.example.guetapp/native";
    private static final String TAG = "MethodChannelHandler";
    
    private Activity activity;
    private FlutterEngine flutterEngine;
    private MethodChannel methodChannel;
    
    // MethodChannel方法名常量
    public static final String METHOD_SHOW_TOAST = "showToast";
    public static final String METHOD_GET_DEVICE_INFO = "getDeviceInfo";
    public static final String METHOD_NAVIGATE_TO_PAGE = "navigateToPage";
    public static final String METHOD_GET_USER_DATA = "getUserData";
    public static final String METHOD_CALL_NATIVE_FUNCTION = "callNativeFunction";
    public static final String METHOD_LOGIN_STATE_CHANGED = "loginStateChanged";
    
    public MethodChannelHandler(Activity activity, FlutterEngine flutterEngine) {
        this.activity = activity;
        this.flutterEngine = flutterEngine;
        setupMethodChannel();
    }
    
    /**
     * 设置MethodChannel
     */
    private void setupMethodChannel() {
        if (flutterEngine != null) {
            methodChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL_NAME);
            methodChannel.setMethodCallHandler(this);
            Log.d(TAG, "MethodChannel setup completed");
        }
    }
    
    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        String method = call.method;
        Log.d(TAG, "Method called: " + method + ", arguments: " + call.arguments);
        
        switch (method) {
            case METHOD_SHOW_TOAST:
                handleShowToast(call, result);
                break;
                
            case METHOD_GET_DEVICE_INFO:
                handleGetDeviceInfo(call, result);
                break;
                
            case METHOD_NAVIGATE_TO_PAGE:
                handleNavigateToPage(call, result);
                break;
                
            case METHOD_GET_USER_DATA:
                handleGetUserData(call, result);
                break;
                
            case METHOD_CALL_NATIVE_FUNCTION:
                handleCallNativeFunction(call, result);
                break;

            case METHOD_LOGIN_STATE_CHANGED:
                handleLoginStateChanged(call, result);
                break;
                
            default:
                Log.w(TAG, "Unknown method: " + method);
                result.notImplemented();
                break;
        }
    }
    
    /**
     * 显示Toast消息
     */
    private void handleShowToast(MethodCall call, MethodChannel.Result result) {
        String message = call.argument("message");
        if (message != null && activity != null) {
            activity.runOnUiThread(() -> {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                result.success(true);
            });
        } else {
            result.error("INVALID_ARGUMENT", "Message cannot be null", null);
        }
    }
    
    /**
     * 获取设备信息
     */
    private void handleGetDeviceInfo(MethodCall call, MethodChannel.Result result) {
        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("model", android.os.Build.MODEL);
        deviceInfo.put("manufacturer", android.os.Build.MANUFACTURER);
        deviceInfo.put("version", android.os.Build.VERSION.RELEASE);
        deviceInfo.put("sdkVersion", android.os.Build.VERSION.SDK_INT);
        deviceInfo.put("brand", android.os.Build.BRAND);
        
        result.success(deviceInfo);
    }
    
    /**
     * 导航到指定页面（原生页面）
     */
    private void handleNavigateToPage(MethodCall call, MethodChannel.Result result) {
        Integer pageIndex = call.argument("pageIndex");
        String pageName = call.argument("page");
        
        if (activity != null && activity instanceof com.example.guetapp.MainActivity) {
            com.example.guetapp.MainActivity mainActivity = (com.example.guetapp.MainActivity) activity;
            activity.runOnUiThread(() -> {
                if (pageIndex != null && pageIndex >= 0 && pageIndex < 4) {
                    mainActivity.navigateToPage(pageIndex);
                    result.success(true);
                } else {
                    result.error("INVALID_PAGE", "Page index must be between 0 and 3", null);
                }
            });
        } else {
            result.error("ACTIVITY_NULL", "Activity is null or not MainActivity", null);
        }
    }
    
    /**
     * 获取用户数据（示例）
     */
    private void handleGetUserData(MethodCall call, MethodChannel.Result result) {
        Map<String, Object> userData = new HashMap<>();
        try {
            android.content.Context ctx = activity != null ? activity.getApplicationContext() : null;
            if (ctx != null) {
                boolean isLoggedIn = com.example.guetapp.manager.SessionManager.isLoggedIn(ctx);
                boolean isGuest = com.example.guetapp.manager.SessionManager.isGuest(ctx);
                String userName = com.example.guetapp.manager.SessionManager.getUserName(ctx);
                String access = com.example.guetapp.manager.SessionManager.getAccessToken(ctx);
                userData.put("isLoggedIn", isLoggedIn);
                userData.put("isGuest", isGuest);
                userData.put("userName", userName);
                userData.put("accessToken", access);
            }
        } catch (Exception e) {
            Log.w(TAG, "getUserData error: " + e.getMessage());
        }
        result.success(userData);
    }
    
    /**
     * 调用原生功能（通用方法）
     */
    private void handleCallNativeFunction(MethodCall call, MethodChannel.Result result) {
        String functionName = call.argument("functionName");
        Map<String, Object> params = call.argument("params");
        
        Log.d(TAG, "Calling native function: " + functionName + ", params: " + params);
        
        if (activity == null) {
            result.error("ACTIVITY_NULL", "Activity is null", null);
            return;
        }
        
        // 根据functionName执行不同的原生功能
        switch (functionName) {
            case "openSettings":
                // 打开设置页面
                Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                activity.startActivity(intent);
                result.success(true);
                break;
                
            case "getBatteryLevel":
                // 获取电池电量（示例）
                result.success(50); // 实际应该从系统获取
                break;
            case "logout":
                handleLogout(result);
                break;
                
            case "navigateToLogin":
                handleNavigateToLogin(result);
                break;
                
            default:
                result.error("UNKNOWN_FUNCTION", "Unknown function: " + functionName, null);
                break;
        }
    }

    /** Flutter侧通知登录状态变化（登录成功） */
    private void handleLoginStateChanged(MethodCall call, MethodChannel.Result result) {
        if (activity == null) {
            result.error("ACTIVITY_NULL", "Activity is null", null);
            return;
        }
        Map<String, Object> args = call.arguments instanceof Map ? (Map<String, Object>) call.arguments : null;
        if (args == null) {
            result.error("INVALID_ARGUMENT", "arguments is null", null);
            return;
        }
        String access = safeString(args.get("accessToken"));
        String refresh = safeString(args.get("refreshToken"));
        String userName = safeString(args.get("userName"));
        com.example.guetapp.manager.SessionManager.setTokens(activity, access, refresh, userName);
        result.success(true);
    }

    /** 退出登录：清理会话并回到登录页 */
    private void handleLogout(MethodChannel.Result result) {
        if (activity == null) {
            result.error("ACTIVITY_NULL", "Activity is null", null);
            return;
        }
        com.example.guetapp.manager.SessionManager.logout(activity);
        Intent intent = new Intent(activity, com.example.guetapp.component.login.activity.LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
        result.success(true);
    }

    /** 跳转到登录页（游客状态） */
    private void handleNavigateToLogin(MethodChannel.Result result) {
        if (activity == null) {
            result.error("ACTIVITY_NULL", "Activity is null", null);
            return;
        }
        Intent intent = new Intent(activity, com.example.guetapp.component.login.activity.LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
        result.success(true);
    }

    private String safeString(Object o) {
        return o == null ? "" : String.valueOf(o);
    }
    
    /**
     * 从原生向Flutter发送消息
     */
    public void sendMessageToFlutter(String method, Object arguments) {
        if (methodChannel != null) {
            activity.runOnUiThread(() -> {
                methodChannel.invokeMethod(method, arguments);
            });
        }
    }
    
    /**
     * 清理资源
     */
    public void dispose() {
        if (methodChannel != null) {
            methodChannel.setMethodCallHandler(null);
            methodChannel = null;
        }
        activity = null;
        flutterEngine = null;
    }
}


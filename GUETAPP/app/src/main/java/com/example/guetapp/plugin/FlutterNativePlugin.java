package com.example.guetapp.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * Flutter与原生Android通信的Plugin
 * 通过MethodChannel实现双向通信
 */
public class FlutterNativePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    
    private static final String CHANNEL_NAME = "com.example.guetapp/native";
    private static final String TAG = "FlutterNativePlugin";
    
    private MethodChannel channel;
    private Context applicationContext;
    private Activity activity;
    
    // MethodChannel方法名常量
    public static final String METHOD_SHOW_TOAST = "showToast";
    public static final String METHOD_GET_DEVICE_INFO = "getDeviceInfo";
    public static final String METHOD_NAVIGATE_TO_PAGE = "navigateToPage";
    public static final String METHOD_GET_USER_DATA = "getUserData";
    public static final String METHOD_CALL_NATIVE_FUNCTION = "callNativeFunction";
    
    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        applicationContext = binding.getApplicationContext();
        channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_NAME);
        channel.setMethodCallHandler(this);
        Log.d(TAG, "FlutterNativePlugin attached to engine");
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        String method = call.method;
        Log.d(TAG, "Method called: " + method);
        
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
                
            default:
                result.notImplemented();
                break;
        }
    }
    
    /**
     * 显示Toast消息
     */
    private void handleShowToast(MethodCall call, Result result) {
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
    private void handleGetDeviceInfo(MethodCall call, Result result) {
        if (applicationContext != null) {
            Bundle deviceInfo = new Bundle();
            deviceInfo.putString("model", android.os.Build.MODEL);
            deviceInfo.putString("manufacturer", android.os.Build.MANUFACTURER);
            deviceInfo.putString("version", android.os.Build.VERSION.RELEASE);
            deviceInfo.putInt("sdkVersion", android.os.Build.VERSION.SDK_INT);
            
            // 转换为Map返回给Flutter
            java.util.Map<String, Object> infoMap = new java.util.HashMap<>();
            infoMap.put("model", deviceInfo.getString("model"));
            infoMap.put("manufacturer", deviceInfo.getString("manufacturer"));
            infoMap.put("version", deviceInfo.getString("version"));
            infoMap.put("sdkVersion", deviceInfo.getInt("sdkVersion"));
            
            result.success(infoMap);
        } else {
            result.error("CONTEXT_NULL", "Application context is null", null);
        }
    }
    
    /**
     * 导航到指定页面（原生页面）
     */
    private void handleNavigateToPage(MethodCall call, Result result) {
        String pageName = call.argument("page");
        int pageIndex = call.argument("pageIndex");
        
        if (activity != null && activity instanceof com.example.guetapp.MainActivity) {
            com.example.guetapp.MainActivity mainActivity = (com.example.guetapp.MainActivity) activity;
            activity.runOnUiThread(() -> {
                if (pageIndex >= 0 && pageIndex < 4) {
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
    private void handleGetUserData(MethodCall call, Result result) {
        // 这里可以从SharedPreferences、数据库等获取用户数据
        java.util.Map<String, Object> userData = new java.util.HashMap<>();
        userData.put("userId", "12345");
        userData.put("userName", "Flutter User");
        userData.put("isLoggedIn", true);
        
        result.success(userData);
    }
    
    /**
     * 调用原生功能（通用方法）
     */
    private void handleCallNativeFunction(MethodCall call, Result result) {
        String functionName = call.argument("functionName");
        java.util.Map<String, Object> params = call.argument("params");
        
        Log.d(TAG, "Calling native function: " + functionName);
        
        // 根据functionName执行不同的原生功能
        if ("openSettings".equals(functionName)) {
            // 打开设置页面
            if (activity != null) {
                Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                activity.startActivity(intent);
                result.success(true);
            } else {
                result.error("ACTIVITY_NULL", "Activity is null", null);
            }
        } else {
            result.error("UNKNOWN_FUNCTION", "Unknown function: " + functionName, null);
        }
    }
    
    /**
     * 从原生向Flutter发送消息
     */
    public void sendMessageToFlutter(String method, Object arguments) {
        if (channel != null) {
            channel.invokeMethod(method, arguments);
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        channel = null;
        applicationContext = null;
        Log.d(TAG, "FlutterNativePlugin detached from engine");
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        Log.d(TAG, "FlutterNativePlugin attached to activity");
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
        Log.d(TAG, "FlutterNativePlugin detached from activity");
    }
}


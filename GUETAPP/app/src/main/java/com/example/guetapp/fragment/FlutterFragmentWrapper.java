package com.example.guetapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.guetapp.R;
import com.example.guetapp.plugin.MethodChannelHandler;

import io.flutter.embedding.android.FlutterFragment;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;

/**
 * Flutter Fragment包装类
 * 用于在原生Android应用中嵌入Flutter页面
 * 支持通过MethodChannel与Flutter进行双向通信
 * 
 * 使用说明：
 * 1. 需要在app/build.gradle中添加Flutter依赖：
 *    dependencies {
 *        implementation project(':flutter')
 *    }
 * 
 * 2. 需要在settings.gradle中配置Flutter模块：
 *    include ':flutter'
 *    project(':flutter').projectDir = new File('../flutter_module')
 * 
 * 3. 集成Flutter后，取消注释下面的代码，并注释掉占位视图相关代码
 */
public class FlutterFragmentWrapper extends BaseFragment {
    
    private static final String TAG = "FlutterFragmentWrapper";
    // 为不同路由使用不同的Engine ID，避免复用同一个Engine导致始终显示首个路由
    private static final String FLUTTER_ENGINE_ID_PREFIX = "flutter_engine_id_";
    
    // Flutter相关
    private FlutterFragment flutterFragment;
    private FlutterEngine flutterEngine;
    private MethodChannelHandler methodChannelHandler;
    private String initialRoute;
    private TextView placeholderView;

    /**
     * 创建FlutterFragment实例
     * @param initialRoute Flutter页面的初始路由
     * @return FlutterFragmentWrapper实例
     */
    public static FlutterFragmentWrapper newInstance(String initialRoute) {
        FlutterFragmentWrapper fragment = new FlutterFragmentWrapper();
        Bundle args = new Bundle();
        args.putString("initialRoute", initialRoute);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initialRoute = getArguments().getString("initialRoute", "/");
        }
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 创建一个容器View来承载FlutterFragment
        View view = inflater.inflate(R.layout.fragment_flutter_container, container, false);
        
        // 尝试初始化Flutter
        try {
            initializeFlutter(view);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Flutter", e);
            // 如果Flutter未集成，显示占位视图
            showPlaceholder(view);
        }
        
        return view;
    }
    
    /**
     * 初始化Flutter Fragment和MethodChannel
     */
    private void initializeFlutter(View containerView) {
        // 检查Flutter类是否可用
        try {
            Class.forName("io.flutter.embedding.android.FlutterFragment");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Flutter classes not found. Please integrate Flutter first.", e);
        }
        
        // 获取或创建Flutter Engine
        flutterEngine = getOrCreateFlutterEngine();
        
        // 设置MethodChannel Handler
        if (flutterEngine != null && getActivity() != null) {
            methodChannelHandler = new MethodChannelHandler(getActivity(), flutterEngine);
            Log.d(TAG, "MethodChannelHandler initialized");
        }
        
        // 创建FlutterFragment
        flutterFragment = FlutterFragment.withCachedEngine(getEngineId())
                .shouldAttachEngineToActivity(true)
                .build();
        
        // 将FlutterFragment添加到容器中
        if (getChildFragmentManager() != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flutter_container, flutterFragment)
                    .commit();
            Log.d(TAG, "FlutterFragment added to container");
        }
    }
    
    /**
     * 获取或创建Flutter Engine
     */
    private FlutterEngine getOrCreateFlutterEngine() {
        FlutterEngineCache engineCache = FlutterEngineCache.getInstance();
        FlutterEngine engine = engineCache.get(getEngineId());
        
        if (engine == null && getActivity() != null) {
            // 创建新的Flutter Engine
            engine = new FlutterEngine(getActivity().getApplicationContext());
            
            // 设置初始路由
            if (initialRoute != null && !initialRoute.isEmpty()) {
                engine.getNavigationChannel().setInitialRoute(initialRoute);
            }
            
            // 执行Dart代码
            engine.getDartExecutor().executeDartEntrypoint(
                    DartExecutor.DartEntrypoint.createDefault()
            );
            
            // 缓存Engine
            engineCache.put(getEngineId(), engine);
            Log.d(TAG, "Flutter Engine created and cached");
        } else {
            Log.d(TAG, "Using cached Flutter Engine");
        }
        
        return engine;
    }

    /**
     * 根据路由生成唯一的Engine ID，确保不同初始路由不会复用同一个Engine
     */
    private String getEngineId() {
        if (initialRoute == null || initialRoute.isEmpty()) {
            return FLUTTER_ENGINE_ID_PREFIX + "root";
        }
        // 替换非法字符
        return FLUTTER_ENGINE_ID_PREFIX + initialRoute.replace("/", "_");
    }
    
    /**
     * 显示占位视图（Flutter未集成时）
     */
    private void showPlaceholder(View view) {
        placeholderView = view.findViewById(R.id.tv_flutter_placeholder);
        if (placeholderView != null) {
            placeholderView.setVisibility(View.VISIBLE);
            placeholderView.setText("Flutter页面: " + initialRoute + "\n（需要集成Flutter Engine）");
        }
    }

    @Override
    protected void initViews() {
        // Flutter视图已通过getLayoutView初始化
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
    public void onDestroyView() {
        super.onDestroyView();
        
        // 清理MethodChannel Handler
        if (methodChannelHandler != null) {
            methodChannelHandler.dispose();
            methodChannelHandler = null;
            Log.d(TAG, "MethodChannelHandler disposed");
        }
        
        // 移除FlutterFragment（需要检查状态，避免在onSaveInstanceState后执行）
        if (flutterFragment != null && getChildFragmentManager() != null) {
            try {
                // 检查FragmentManager状态，只有在安全的情况下才执行事务
                if (isAdded() && !getChildFragmentManager().isStateSaved()) {
                    getChildFragmentManager()
                            .beginTransaction()
                            .remove(flutterFragment)
                            .commit();
                    Log.d(TAG, "FlutterFragment removed");
                } else {
                    // 如果状态已保存，使用commitAllowingStateLoss（不推荐但安全）
                    getChildFragmentManager()
                            .beginTransaction()
                            .remove(flutterFragment)
                            .commitAllowingStateLoss();
                    Log.d(TAG, "FlutterFragment removed (allowing state loss)");
                }
            } catch (IllegalStateException e) {
                // 如果仍然失败，记录错误但不崩溃
                Log.w(TAG, "Failed to remove FlutterFragment: " + e.getMessage());
            }
            flutterFragment = null;
        }
    }
    
    /**
     * 获取MethodChannel Handler（用于从原生向Flutter发送消息）
     */
    public MethodChannelHandler getMethodChannelHandler() {
        return methodChannelHandler;
    }
    
    /**
     * 获取Flutter Engine
     */
    public FlutterEngine getFlutterEngine() {
        return flutterEngine;
    }
}


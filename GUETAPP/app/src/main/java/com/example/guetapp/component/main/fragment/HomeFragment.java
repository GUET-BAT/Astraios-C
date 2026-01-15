package com.example.guetapp.component.main.fragment;

import android.os.Bundle;

import com.example.guetapp.component.flutter.fragment.FlutterFragmentWrapper;

/**
 * 主页Fragment（使用Flutter实现）
 */
public class HomeFragment extends FlutterFragmentWrapper {
    
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("initialRoute", "/home");
        fragment.setArguments(args);
        return fragment;
    }
}


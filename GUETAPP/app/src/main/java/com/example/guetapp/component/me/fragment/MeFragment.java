package com.example.guetapp.component.me.fragment;

import android.os.Bundle;

import com.example.guetapp.component.flutter.fragment.FlutterFragmentWrapper;

/**
 * 我的页面Fragment（使用Flutter实现）
 */
public class MeFragment extends FlutterFragmentWrapper {
    
    public static MeFragment newInstance() {
        MeFragment fragment = new MeFragment();
        Bundle args = new Bundle();
        args.putString("initialRoute", "/me");
        fragment.setArguments(args);
        return fragment;
    }
}


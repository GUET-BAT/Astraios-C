package com.example.guetapp.fragment;

import android.os.Bundle;

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


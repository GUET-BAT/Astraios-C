package com.example.guetapp.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 全局会话管理：登录态/游客态
 *
 * - 登录成功：setLoggedIn(true) 且 setGuest(false)
 * - 游客模式：setGuest(true) 且 setLoggedIn(false)
 */
public final class SessionManager {
    private static final String SP_NAME = "guetapp_session";

    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_GUEST = "guest";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_NAME = "user_name";

    private SessionManager() {}

    private static SharedPreferences sp(Context context) {
        return context.getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    /** 是否已登录（账号密码/手机号等） */
    public static boolean isLoggedIn(Context context) {
        return sp(context).getBoolean(KEY_LOGGED_IN, false);
    }

    /** 是否游客模式 */
    public static boolean isGuest(Context context) {
        return sp(context).getBoolean(KEY_GUEST, false);
    }

    /** 是否允许进入主页（已登录 或 游客模式） */
    public static boolean canEnterHome(Context context) {
        return isLoggedIn(context) || isGuest(context);
    }

    /** 设置登录态（会自动清除游客态） */
    public static void setLoggedIn(Context context, boolean loggedIn) {
        sp(context).edit()
                .putBoolean(KEY_LOGGED_IN, loggedIn)
                .putBoolean(KEY_GUEST, false)
                .apply();
    }

    /** 设置游客态（会自动清除登录态） */
    public static void setGuest(Context context, boolean guest) {
        sp(context).edit()
                .putBoolean(KEY_GUEST, guest)
                .putBoolean(KEY_LOGGED_IN, false)
                .apply();
    }

    /** 退出登录/退出游客：回到未认证状态 */
    public static void logout(Context context) {
        sp(context).edit()
                .putBoolean(KEY_LOGGED_IN, false)
                .putBoolean(KEY_GUEST, false)
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .remove(KEY_USER_NAME)
                .apply();
    }

    /** 设置登录凭证与用户名 */
    public static void setTokens(Context context, String accessToken, String refreshToken, String userName) {
        sp(context).edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putBoolean(KEY_GUEST, false)
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putString(KEY_USER_NAME, userName)
                .apply();
    }

    public static String getAccessToken(Context context) {
        return sp(context).getString(KEY_ACCESS_TOKEN, "");
    }

    public static String getRefreshToken(Context context) {
        return sp(context).getString(KEY_REFRESH_TOKEN, "");
    }

    public static String getUserName(Context context) {
        return sp(context).getString(KEY_USER_NAME, "");
    }
}



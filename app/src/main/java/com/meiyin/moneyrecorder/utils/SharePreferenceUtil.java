package com.meiyin.moneyrecorder.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cootek332 on 18/4/1.
 */

public class SharePreferenceUtil {
    private static SharedPreferences mSharedPreferences;
    public static void init(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("main_pref", Context.MODE_PRIVATE);
        }
    }

    public static void setRecord(String key, String value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public static String getStringRecord(String key) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getString(key, "");
        }
        return "";
    }

    public static void setRecord(String key, int value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(key, value);
            editor.commit();
        }
    }
    public static int getIntRecord(String key) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getInt(key, -1);
        }
        return -1;
    }

    public static void setRecord(String key, boolean value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }
    }
    public static boolean getBooleanRecord(String key) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getBoolean(key, false);
        }
        return false;
    }



}

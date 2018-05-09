package com.meiyin.moneyrecorder.utils;

import android.content.res.Resources;

public class CommonUtil {

    public static float dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }
}

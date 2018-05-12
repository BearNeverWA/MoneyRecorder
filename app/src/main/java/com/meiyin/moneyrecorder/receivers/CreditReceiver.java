package com.meiyin.moneyrecorder.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.activities.PersonalCenterActivity;
import com.meiyin.moneyrecorder.entities.CreditItems;
import com.meiyin.moneyrecorder.sqlite.SQLiteUtils;
import com.meiyin.moneyrecorder.utils.CreditUtil;
import com.meiyin.moneyrecorder.utils.SharePreferenceKeys;
import com.meiyin.moneyrecorder.utils.SharePreferenceUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by cootek332 on 18/5/11.
 */

public class CreditReceiver extends BroadcastReceiver {
    private static final String TAG = "CreditReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        int currentDate = Integer.parseInt(dateFormat.format(calendar.getTime()));
        int currentHour = Integer.parseInt(hourFormat.format(calendar.getTime()));
//        ArrayList<CreditItems> creditItems = SQLiteUtils.getCredits();
        if (currentDate == 1 && currentHour < 9) {
            SharePreferenceUtil.setRecord(SharePreferenceKeys.KEY_WARNED_THIS_MONTH_CARD_1, false);
            SharePreferenceUtil.setRecord(SharePreferenceKeys.KEY_WARNED_THIS_MONTH_CARD_2, false);
        }
        int warningCount = CreditUtil.getWarningCount(currentDate);
        String current_key = SharePreferenceKeys.KEY_WARNED_THIS_MONTH_CARD_1 ;
        if (warningCount == 1) {
            current_key = SharePreferenceKeys.KEY_WARNED_THIS_MONTH_CARD_2 ;
        }
        if (warningCount != -1
               && !SharePreferenceUtil.getBooleanRecord(current_key)) {
            Intent intent1 = new Intent(context, PersonalCenterActivity.class);
            if (PersonalCenterActivity.mActivity != null) {
                PersonalCenterActivity.mActivity.finish();
            }
            intent1.putExtra("msg", "need show dialog");
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("信用卡还款提醒")
                    .setContentText("今天是您信用卡的还款日哦")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            notificationManager.notify(0, builder.build());

        }
    }
}

package com.meiyin.moneyrecorder.utils;

import com.meiyin.moneyrecorder.http.entities.card;
import com.meiyin.moneyrecorder.sqlite.SQLiteUtils;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by cootek332 on 18/5/23.
 */

public class BmobUtils {
    public static void uploadCredits(String sUserName, String sFamilyName, String sCardNumber, String sBankName, int iPayDay, int iBillDay, SaveListener<String> callback) {
        card sCard = new card();
        sCard.setUserName(sUserName);
        sCard.setFamilyname(sFamilyName);
        sCard.setCardNumber(sCardNumber);
        sCard.setBankName(sBankName);
        sCard.setPayDay(iPayDay);
        sCard.setBillDay(iBillDay);
        sCard.save(callback);
    }

    public static void getCredits(String sUserName, QueryListener callback) {
        BmobQuery query = new BmobQuery("card");
        query.addWhereEqualTo("sUserName", sUserName);
        query.findObjectsByTable(callback);
    }

    public static void deleteCredits(final String objectId, final String id) {
        card sCard = new card();
        sCard.delete(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    SQLiteUtils.deleteCredit(id);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}

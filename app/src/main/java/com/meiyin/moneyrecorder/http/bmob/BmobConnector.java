package com.meiyin.moneyrecorder.http.bmob;

import com.meiyin.moneyrecorder.http.entities.card;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by cootek332 on 18/5/23.
 */

public class BmobConnector {
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
}

package com.meiyin.moneyrecorder.utils;

import com.meiyin.moneyrecorder.entities.CreditItems;
import com.meiyin.moneyrecorder.sqlite.SQLiteUtils;

import java.util.ArrayList;

/**
 * Created by cootek332 on 18/5/12.
 */

public class CreditUtil {
    private static ArrayList<CreditItems> creditsItems;

    private static CreditItems warningCreditItems;
    private static int count = -1;

    public static CreditItems getWarningCreditItems(int date) {
        warningCreditItems = null;
        hasItemToWarning(date);
        return warningCreditItems;
    }

    public static int getWarningCount(int date) {
        count = -1;
        hasItemToWarning(date);
        return count;
    }

    private static boolean hasItemToWarning(int date) {
        boolean result = false;
        creditsItems = SQLiteUtils.getCredits();
        for (int i = 0; i < creditsItems.size(); i++) {
            if (creditsItems.get(i).getPayDay() == date) {
                warningCreditItems = creditsItems.get(i);
                count = i;
                result = true;
            }
        }
        return result;
    }



}

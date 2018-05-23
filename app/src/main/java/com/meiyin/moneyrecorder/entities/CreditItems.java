package com.meiyin.moneyrecorder.entities;

/**
 * Created by cootek332 on 18/4/4.
 */

public class CreditItems {
    private String objectId;
    private String id;
    private String bankName;
    private String cardNumber;
    private int billDay;
    private int payDay;
    private int deleted;
    private int uploaded;

    public CreditItems(String objectId, String id, String bankName, String cardNumber, int billDay, int payDay, int deleted, int uploaded) {
        this.objectId = objectId;
        this.id = id;
        this.bankName = bankName;
        this.cardNumber = cardNumber;
        this.billDay = billDay;
        this.payDay = payDay;
        this.deleted = deleted;
        this.uploaded = uploaded;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getId() {
        return id;
    }

    public String getBankName() {
        return bankName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getBillDay() {
        return billDay;
    }

    public int getPayDay() {
        return payDay;
    }

    public int getDeleted() {
        return deleted;
    }

    public int getUploaded() {
        return uploaded;
    }

}

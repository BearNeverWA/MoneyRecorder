package com.meiyin.moneyrecorder.entities;

/**
 * Created by cootek332 on 18/4/4.
 */

public class RecordItems {
    private String objectId;
    private String id;
    private String buyClassifyOne;
    private String payClassify;
    private double money;
    private String setTime;
    private long recordTime;
    private int deleted;

    public RecordItems(String objectId, String id, String buyClassifyOne, String payClassify, double money, String setTime, long recordTime, int deleted) {
        this.objectId = objectId;
        this.id = id;
        this.buyClassifyOne = buyClassifyOne;
        this.payClassify = payClassify;
        this.money = money;
        this.setTime = setTime;
        this.recordTime = recordTime;
        this.deleted = deleted;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getId() {
        return id;
    }

    public String getBuyClassifyOne() {
        return buyClassifyOne;
    }

    public String getSetTime() {
        return setTime;
    }

    public double getMoney() {
        return money;
    }

    public String getPayClassify() {
        return payClassify;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public int getDeleted() {
        return deleted;
    }

}

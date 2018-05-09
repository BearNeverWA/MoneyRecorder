package com.meiyin.moneyrecorder.entities;

public class ListItem {

    private String day;
    private String dayOfWeek;
    private String type;
    private boolean inOrCome;  //true is in,false is out
    private String detail;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isInOrCome() {
        return inOrCome;
    }

    public void setInOrCome(boolean inOrCome) {
        this.inOrCome = inOrCome;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}

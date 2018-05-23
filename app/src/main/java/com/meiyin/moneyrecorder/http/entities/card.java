package com.meiyin.moneyrecorder.http.entities;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
/**
 * Created by jmy on 2018/5/15.
 */

public class card extends BmobObject{
    private Integer id;
    private String sBankName;
    private String sCardNumber;
    private Integer iBillDay;
    private Integer iPayDay;
    private Integer iDeleted;
    private Integer iUploaded;
    private String sUserName;
    private String sFamilyName;

    public Integer getId(){
        return id;
    }

    public String getBankName(){
       return sBankName;
    }

    public void setBankName(String sBankName){
        this.sBankName = sBankName;
    }

    public String getCardNumber(){
        return sCardNumber;
    }

    public void setCardNumber(String sCardNumber){
        this.sCardNumber = sCardNumber;
    }

    public Integer getBillDay(){
        return iBillDay;
    }

    public void setBillDay(Integer iBillDay){
        this.iBillDay = iBillDay;
    }

    public Integer getPayDay(){
        return iPayDay;
    }

    public void setPayDay(Integer iPayDay){
        this.iPayDay = iPayDay;
    }

    public Integer getDeleted(){
        return iDeleted;
    }

    public void setDeleted(Integer iDeleted){
        this.iDeleted = iDeleted;
    }

    public Integer getUploaded(){
        return iUploaded;
    }

    public void setUploaded(Integer iUploaded){
        this.iUploaded = iUploaded;
    }

    public String getUserName(){
        return sUserName;
    }

    public void setUserName(String sUserName){
        this.sUserName = sUserName;
    }

    public String getFamilyname(){
        return sFamilyName;
    }

    public void setFamilyname(String sFamilyName){
        this.sFamilyName = sFamilyName;
    }
}

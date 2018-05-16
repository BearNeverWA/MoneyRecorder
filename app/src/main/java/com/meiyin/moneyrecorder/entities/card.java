package com.meiyin.moneyrecorder.entities;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
/**
 * Created by jmy on 2018/5/15.
 */

public class card extends BmobObject{
    private Integer id;
    private String sBankName;
    private String sCardNumber;
    private BmobDate iBillDay;
    private BmobDate iPayDay;
    private Integer iDeleted;
    private Integer iUploaded;
    private String user_name;
    private String familyname;
    public Integer getId(){
        return id;
    }
    public String getsBankName(){
       return sBankName;
    }
    public void setsBankName(String sBankName){
        this.sBankName=sBankName;
    }
    public String getsCardNumber(){
        return sCardNumber;
    }
    public void setsCardNumber(String sCardNumber){
        this.sCardNumber=sCardNumber;
    }
    public BmobDate getiBillDay(){
        return iBillDay;
    }
    public void setiBillDay(BmobDate iBillDay){
        this.iBillDay=iBillDay;
    }
    public BmobDate getiPayDay(){
        return iPayDay;
    }
    public void setiPayDay(BmobDate iPayDay){
        this.iPayDay=iPayDay;
    }
    public Integer getiDeleted(){
        return iDeleted;
    }
    public void setiDeleted(Integer iDeleted){
        this.iDeleted=iDeleted;
    }
    public Integer getiUploaded(){
        return iUploaded;
    }
    public void setiUploaded(Integer iUploaded){
        this.iUploaded=iUploaded;
    }
    public String getUser_name(){
        return user_name;
    }
    public void setUser_name(String user_name){
        this.user_name=user_name;
    }
    public String getFamilyname(){
        return familyname;
    }
    public void setFamilyname(String familyname){
        this.familyname=familyname;
    }
}

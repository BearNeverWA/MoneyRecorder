package com.meiyin.moneyrecorder.entities;
import cn.bmob.v3.BmobObject;
/**
 * Created by jmy on 2018/5/14.
 */

public class record_table extends BmobObject {
    private Integer id;
    private String sUserName;
    private String sFamilyName;
    private String sBuyClassifyOne;
    private String sPayClassify;
    private Double rMoney;
    private String sTime;
    private String iCurrentTime;
    private Integer iDeleted;
    private Integer iUploaded;
    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id=id;
    }
    public String getsUserName() {
        return sUserName;
    }
    public void setsUserName(String sUserName) {
        this.sUserName = sUserName;
    }
    public String getsFamilyName() {
        return sFamilyName;
    }
    public void setsFamilyName(String sFamilyName) {
        this.sFamilyName = sFamilyName;
    }
    public String getsBuyClassifyOne(){
        return sBuyClassifyOne;
    }
    public void setsBuyClassifyOne(String sBuyClassifyOne){
        this.sBuyClassifyOne=sBuyClassifyOne;
    }
    public String getsPayClassify(){
        return sPayClassify;
    }
    public void setsPayClassify(String sPayClassify){
        this.sPayClassify=sPayClassify;
    }
    public Double getrMoney(){
        return rMoney;
    }
    public void setrMoney(Double rMoney){
        this.rMoney=rMoney;
    }
    public String getsTime(){
        return sTime;
    }
    public void setsTime(String sTime){
        this.sTime=sTime;
    }
    public String getiCurrentTime(){
        return iCurrentTime;
    }
    public void setiCurrentTime(String iCurrentTime){
        this.iCurrentTime=iCurrentTime;
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
}

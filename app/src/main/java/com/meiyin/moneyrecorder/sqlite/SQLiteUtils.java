package com.meiyin.moneyrecorder.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.meiyin.moneyrecorder.entities.CreditItems;
import com.meiyin.moneyrecorder.entities.RecordItems;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cootek332 on 18/4/4.
 */

public class SQLiteUtils {

    private static SQLiteDatabase recordDb;
    private static final String RECORD_TABLE_NAME = "record_table";
    private static final String CREDIT_TABLE_NAME = "credit_table";
    private static String TAG = "SQLiteUtils";

    //初始化数据库
    public static void init() {
        //存储目录,在当前app对应目录下的/databases/recorder.db
        File db_dir = new File("/data/data/com.meiyin.moneyrecorder/databases");
        //创建文件夹
        if (!db_dir.exists()) {
            db_dir.mkdirs();
        }
        //创建/打开数据库文件
        if (recordDb == null) {
            recordDb = SQLiteDatabase.openOrCreateDatabase("/data/data/com.meiyin.moneyrecorder/databases/recorder.db", null);
        }
        //如果table不存在,则创建table, 名为"record_table"
        if (!isTableExist(recordDb, RECORD_TABLE_NAME)) {
            try {
                createRecordTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //如果table不存在,则创建table, 名为"credit_table"
        if (!isTableExist(recordDb, CREDIT_TABLE_NAME)) {
            try {
                createCreditTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
        table的字段有:
        系统自增变量 _id (int)
        消费/收入一级类型 sBuyClassifyOne (text)

     */
    public static void createRecordTable() {
        if (recordDb == null) {
            Log.e(TAG, "createTable db is null");
            return;
        }
        String table_sql = "create table " + RECORD_TABLE_NAME + "(_id integer primary key autoincrement, sObjectId text, sBuyClassifyOne text, sPayClassify text, rMoney real, sTime text, iCurrentTime text, iDeleted int, iUploaded int)";
        recordDb.execSQL(table_sql);
    }

    public static void createCreditTable() {
        if (recordDb == null) {
            Log.e(TAG, "createTable db is null");
            return;
        }
        String table_sql = "create table " + CREDIT_TABLE_NAME + "(_id integer primary key autoincrement, sBankName text, sCardNumber text, iBillDay int, iPayDay int, iDeleted int, iUploaded int)";
        recordDb.execSQL(table_sql);
    }

    private static void dropTable(String table_name) {
        String truncate_record_table_sql = "drop table " + table_name;
        recordDb.execSQL(truncate_record_table_sql);
    }

    public static void insertRecord(RecordItems record) {
        //(String buyClassifyOne,String payClassify,  Double money, String time, long currentTime, int deleted) {
        if (recordDb == null) {
            Log.e(TAG, "insertRecord db is null");
            return;
        }
        Log.e (TAG, "insertRecord: " + record.getObjectId());
        ContentValues cValue = new ContentValues();
        cValue.put("sObjectId", record.getObjectId());
        cValue.put("sBuyClassifyOne", record.getBuyClassifyOne());
        cValue.put("sPayClassify", record.getPayClassify());
        cValue.put("rMoney", record.getMoney());
        cValue.put("sTime", record.getSetTime());
        cValue.put("iCurrentTime", record.getRecordTime());
        cValue.put("iDeleted", record.getDeleted());
        cValue.put("iUploaded", record.getUploaded());
        recordDb.insert(RECORD_TABLE_NAME, null, cValue);
    }

    public static void insertCredit(CreditItems credit) {
        if (recordDb == null) {
            Log.e(TAG, "insertRecord db is null");
            return;
        }
        ContentValues cValue = new ContentValues();
        cValue.put("sBankName", credit.getBankName());
        cValue.put("sCardNumber", credit.getCardNumber());
        cValue.put("iBillDay", credit.getBillDay());
        cValue.put("iPayDay", credit.getPayDay());
        cValue.put("iDeleted", credit.getDeleted());
        cValue.put("iUploaded", 0);
        recordDb.insert(CREDIT_TABLE_NAME, null, cValue);
    }

    public static void deleteRecord(String id) {
        ContentValues cValues = new ContentValues();
        cValues.put("iDeleted", 1);
        int i = recordDb.update(RECORD_TABLE_NAME, cValues, "_id = ?", new String[]{id});
        Log.e(TAG, "Delete: " + i);
    }

    public static String getObjectIdFromId(String id) {

        ArrayList<RecordItems> records = new ArrayList<>();
        ArrayList<HashMap<String, Object>> dbData;
        Cursor cursor = recordDb.query(RECORD_TABLE_NAME, null, "_id LIKE ?", new String[]{id + "%"}, null, null, null);
        dbData = cursorToArrList(cursor);
        for (int i = 0; i < dbData.size(); i++) {
            String objectId = (String) dbData.get(i).get("sObjectId");
            String buyClassifyOne = (String) dbData.get(i).get("sBuyClassifyOne");
            String payClassify = (String) dbData.get(i).get("sPayClassify");
            double money = Double.parseDouble((String) dbData.get(i).get("rMoney"));
            String time = (String) dbData.get(i).get("sTime");
            String currentTime = (String) dbData.get(i).get("iCurrentTime");
            int deleted = Integer.parseInt((String) dbData.get(i).get("iDeleted"));
            int uploaded = Integer.parseInt((String) dbData.get(i).get("iUploaded"));
            if (deleted != 1) {
                records.add(new RecordItems(objectId, id, buyClassifyOne, payClassify, money, time, currentTime, deleted, uploaded));
            }
        }
        return records.get(0).getObjectId();
    }

    public static void uploadRecord(String objectId, String iCurrentTime) {
        ContentValues cValues = new ContentValues();
        cValues.put("iUploaded", 1);
        cValues.put("sObjectId", objectId);
        int i = recordDb.update(RECORD_TABLE_NAME, cValues, "iCurrentTime = ?", new String[]{iCurrentTime});
    }

    public static void uploadRecord(String objectId) {
        ContentValues cValues = new ContentValues();
        cValues.put("iUploaded", 1);
        int i = recordDb.update(RECORD_TABLE_NAME, cValues, "sObjectId = ?", new String[]{objectId});
    }

    public static void uploadedCredit(String objectId, String cardNumber) {
        ContentValues cValues = new ContentValues();
        cValues.put("iUploaded", 1);
        cValues.put("sObjectId", objectId);
        int i = recordDb.update(CREDIT_TABLE_NAME, cValues, "sCardNumber = ?", new String[]{cardNumber});
    }

    public static void deleteCredit(String id) {
        ContentValues cValues = new ContentValues();
        cValues.put("iDeleted", 1);
        recordDb.update(CREDIT_TABLE_NAME, cValues, "_id = ?", new String[] {id});
    }

    public static ArrayList<RecordItems> getAllRecords() {
        ArrayList<RecordItems> records = new ArrayList<>();
        ArrayList<HashMap<String, Object>> dbData;
        Cursor cursor = recordDb.query(RECORD_TABLE_NAME, null, null, null, null, null, null);
        dbData = cursorToArrList(cursor);
        for (int i = 0; i < dbData.size(); i++) {
            String objectId = (String) dbData.get(i).get("sObjectId");
            String id = (String) dbData.get(i).get("_id");
            String buyClassifyOne = (String) dbData.get(i).get("sBuyClassifyOne");
            String payClassify = (String) dbData.get(i).get("sPayClassify");
            double money = Double.parseDouble((String) dbData.get(i).get("rMoney"));
            String time = (String) dbData.get(i).get("sTime");
            String currentTime = (String) dbData.get(i).get("iCurrentTime");
            int deleted = Integer.parseInt((String) dbData.get(i).get("iDeleted"));
            int uploaded = Integer.parseInt((String) dbData.get(i).get("iUploaded"));
            records.add(new RecordItems(objectId, id, buyClassifyOne, payClassify, money, time, currentTime, deleted, uploaded));
        }
        return records;
    }

    public static ArrayList<RecordItems> getUnDeletedRecords(String str) {
        ArrayList<RecordItems> records = new ArrayList<>();
        ArrayList<HashMap<String, Object>> dbData;
        Cursor cursor = recordDb.query(RECORD_TABLE_NAME, null, "sTime LIKE ?", new String[]{str + "%"}, null, null, null);
        dbData = cursorToArrList(cursor);
        for (int i = 0; i < dbData.size(); i++) {
            String objectId = (String) dbData.get(i).get("sObjectId");
            String id = (String) dbData.get(i).get("_id");
            String buyClassifyOne = (String) dbData.get(i).get("sBuyClassifyOne");
            String payClassify = (String) dbData.get(i).get("sPayClassify");
            double money = Double.parseDouble((String) dbData.get(i).get("rMoney"));
            String time = (String) dbData.get(i).get("sTime");
            String currentTime = (String) dbData.get(i).get("iCurrentTime");
            int deleted = Integer.parseInt((String) dbData.get(i).get("iDeleted"));
            int uploaded = Integer.parseInt((String) dbData.get(i).get("iUploaded"));
            if (deleted != 1) {
                records.add(new RecordItems(objectId, id, buyClassifyOne, payClassify, money, time, currentTime, deleted, uploaded));
            }
        }
        return records;
    }

    public static ArrayList<RecordItems> getUnUploadedRecords() {
        ArrayList<RecordItems> records = new ArrayList<>();
        ArrayList<HashMap<String, Object>> dbData;
        Cursor cursor = recordDb.query(RECORD_TABLE_NAME, null, "iUploaded LIKE ?", new String[]{"0" + "%"}, null, null, null);
        dbData = cursorToArrList(cursor);
        for (int i = 0; i < dbData.size(); i++) {
            String objectId = (String) dbData.get(i).get("sObjectId");
            String id = (String) dbData.get(i).get("_id");
            String buyClassifyOne = (String) dbData.get(i).get("sBuyClassifyOne");
            String payClassify = (String) dbData.get(i).get("sPayClassify");
            double money = Double.parseDouble((String) dbData.get(i).get("rMoney"));
            String time = (String) dbData.get(i).get("sTime");
            String currentTime = (String) dbData.get(i).get("iCurrentTime");
            int deleted = Integer.parseInt((String) dbData.get(i).get("iDeleted"));
            int uploaded = Integer.parseInt((String) dbData.get(i).get("iUploaded"));
            records.add(new RecordItems(objectId, id, buyClassifyOne, payClassify, money, time, currentTime, deleted, uploaded));
        }
        return records;
    }

    public static ArrayList<CreditItems> getCredits() {
        ArrayList<CreditItems> credits = new ArrayList<>();
        ArrayList<HashMap<String, Object>> dbData;
        Cursor cursor = recordDb.query(CREDIT_TABLE_NAME, null, null, null, null, null, null);
        dbData = cursorToArrList(cursor);
        for (int i = 0; i < dbData.size(); i++) {
            String id = (String)dbData.get(i).get("_id");
            String bankName = (String)dbData.get(i).get("sBankName");
            String cardNumber = (String)dbData.get(i).get("sCardNumber");
            int billDay = Integer.parseInt((String)dbData.get(i).get("iBillDay"));
            int payDay = Integer.parseInt((String)dbData.get(i).get("iPayDay"));
            int deleted = Integer.parseInt((String)dbData.get(i).get("iDeleted"));
            int upload = Integer.parseInt((String)dbData.get(i).get("iUploaded"));
            if (deleted != 1) {
                credits.add(new CreditItems(id, bankName, cardNumber, billDay, payDay, deleted, upload));
            }
        }
        return credits;
    }

    public static ArrayList<CreditItems> getAllCredits() {
        ArrayList<CreditItems> credits = new ArrayList<>();
        ArrayList<HashMap<String, Object>> dbData;
        Cursor cursor = recordDb.query(CREDIT_TABLE_NAME, null, null, null, null, null, null);
        dbData = cursorToArrList(cursor);
        for (int i = 0; i < dbData.size(); i++) {
            String id = (String)dbData.get(i).get("_id");
            String bankName = (String)dbData.get(i).get("sBankName");
            String cardNumber = (String)dbData.get(i).get("sCardNumber");
            int billDay = Integer.parseInt((String)dbData.get(i).get("iBillDay"));
            int payDay = Integer.parseInt((String)dbData.get(i).get("iPayDay"));
            int deleted = Integer.parseInt((String)dbData.get(i).get("iDeleted"));
            int upload = Integer.parseInt((String)dbData.get(i).get("iUploaded"));
            credits.add(new CreditItems(id, bankName, cardNumber, billDay, payDay, deleted, upload));
        }
        return credits;
    }

    public static void clearAll() {
        //如果record_table存在,则删除table
        if (isTableExist(recordDb, RECORD_TABLE_NAME)) {
            try {
                dropTable(RECORD_TABLE_NAME);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (isTableExist(recordDb, CREDIT_TABLE_NAME)) {
            try {
                dropTable(CREDIT_TABLE_NAME);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isTableExist(SQLiteDatabase db, String tableName) {
        boolean result = false;
        if (tableName == null)
            return false;
        Cursor cursor = null;
        String sql = "select * from sqlite_master where type ='table' and name ='" + tableName + "'";
        cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            int count = cursor.getCount();
            if (count > 0) {
                result = true;
            }
        }

        return result;
    }

    private static ArrayList<HashMap<String, Object>> cursorToArrList(Cursor cursor) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map;
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                map.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndexOrThrow(cursor.getColumnName(i))));
            }
            list.add(map);
        }
        return list;
    }
}

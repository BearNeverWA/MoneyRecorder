package com.meiyin.moneyrecorder.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
    private static String TAG = "SQLiteUtils";

    //初始化数据库
    public static void init() {
        //存储目录,在当前app对应目录下的/databasea/recorder.db
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
        String table_sql = "create table " + RECORD_TABLE_NAME + "(_id integer primary key autoincrement, sBuyClassifyOne text, sPayClassify text, rMoney real, sTime text, iCurrentTime int, iDeleted int, iUploaded int)";
        recordDb.execSQL(table_sql);
    }

    private static void clearRecordTable() {
        String truncate_record_table_sql = "drop table " + RECORD_TABLE_NAME;
        recordDb.execSQL(truncate_record_table_sql);
    }

    public static void insertRecord(RecordItems record) {//(String buyClassifyOne,String payClassify,  Double money, String time, long currentTime, int deleted) {
        if (recordDb == null) {
            Log.e(TAG, "insertRecord db is null");
            return;
        }
        ContentValues cValue = new ContentValues();
        cValue.put("sBuyClassifyOne", record.getBuyClassifyOne());
        cValue.put("sPayClassify", record.getPayClassify());
        cValue.put("rMoney", record.getMoney());
        cValue.put("sTime", record.getSetTime());
        cValue.put("iCurrentTime", record.getRecordTime());
        cValue.put("iDeleted", record.getDeleted());
        cValue.put("iUploaded", 0);
        recordDb.insert(RECORD_TABLE_NAME, null, cValue);
    }

    public static void delete(String id) {
        ContentValues cValues = new ContentValues();
        cValues.put("iDeleted", 1);
        recordDb.update(RECORD_TABLE_NAME, cValues, "_id = ?", new String[] {id});
    }

    public static ArrayList<RecordItems> getRecords() {
        ArrayList<RecordItems> records = new ArrayList<>();
        ArrayList<HashMap<String, Object>> dbData;
        Cursor cursor = recordDb.query(RECORD_TABLE_NAME, null, null, null, null, null, null);
        dbData = cursorToArrList(cursor);
        for (int i = 0; i < dbData.size(); i++) {
            String id = (String)dbData.get(i).get("_id");
            String buyClassifyOne = (String)dbData.get(i).get("sBuyClassifyOne");
            String payClassify = (String)dbData.get(i).get("sPayClassify");
            double money = Double.parseDouble((String)dbData.get(i).get("rMoney"));
            String time = (String)dbData.get(i).get("sTime");
            long currentTime = Long.parseLong((String)dbData.get(i).get("iCurrentTime"));
            int deleted = Integer.parseInt((String)dbData.get(i).get("iDeleted"));
            if (deleted != 1) {
                records.add(new RecordItems(id, buyClassifyOne, payClassify, money, time, currentTime, deleted));
            }
        }
        return records;
    }

    public static void clearAll() {
        //如果record_table存在,则删除table
        if (isTableExist(recordDb, RECORD_TABLE_NAME)) {
            try {
                clearRecordTable();
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
        String sql = "select * from sqlite_master where type ='table' and name ='" + tableName + "'" ;
        cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            int count = cursor.getCount();
            if (count > 0) {
                result = true;
            }
        }

        return result;
    }

    private static ArrayList<HashMap<String,Object>> cursorToArrList(Cursor cursor){
        ArrayList<HashMap<String,Object>> list = new ArrayList<>();
        HashMap<String,Object> map;
        while (cursor.moveToNext()){
            map = new HashMap<>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                map.put(cursor.getColumnName(i),cursor.getString(cursor.getColumnIndexOrThrow(cursor.getColumnName(i))));
            }
            list.add(map);
        }
        return list;
    }
}

package com.meiyin.moneyrecorder.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.meiyin.moneyrecorder.BaseActivity;
import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.adapter.ListAdapter;
import com.meiyin.moneyrecorder.entities.ListItem;
import com.meiyin.moneyrecorder.entities.RecordItems;
import com.meiyin.moneyrecorder.entities.record_table;
import com.meiyin.moneyrecorder.sqlite.SQLiteUtils;
import com.meiyin.moneyrecorder.utils.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by cootek332 on 18/4/1.
 */

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    public static Activity mActivity;
    ArrayList<RecordItems> dataFromDb;
    ListAdapter adapter;
    static List<Map<String, Object>> listItems;
//    List<Map<String, Object>> reverseList;

    private TextView tvYear, tvMonth, tvIncome, tvPay, tvStatus, tvNumber, tvPersonalCenter;

    private double inMoney, outMoney, totalMoney;

    private String selectMonth = "";
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteUtils.init();
        initAlarm();
        initData();
        initUI();
        bindEvents();
        mActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initUI();
    }

    private void initData() {
        getLocalData(selectMonth);
        initItems();
        getTotalMoney();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listItems != null) {
            listItems.clear();
            listItems = null;
        }
    }

    private void initItems() {
        if (listItems == null) {
            listItems = new ArrayList<>();
        }
        listItems.clear();
        for (int i = 0; i < dataFromDb.size(); i++) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("objectId", dataFromDb.get(i).getObjectId());
            listItem.put("_id", dataFromDb.get(i).getId());
            listItem.put("buyClassifyOne", dataFromDb.get(i).getBuyClassifyOne());
            listItem.put("payClassify", dataFromDb.get(i).getPayClassify());
            String money = dataFromDb.get(i).getMoney() + "";
            if (!money.contains("-")) {
                money = "+" + money;
            }
            listItem.put("money", money);
            listItem.put("setTime", dataFromDb.get(i).getSetTime().split("年")[1]);
            listItem.put("completeTime", dataFromDb.get(i).getSetTime());
            listItems.add(listItem);
        }
//        reverseList = new ArrayList<>();
//        for (int i = listItems.size() - 1; i >= 0; i--) {
//            reverseList.add(listItems.get(i));
//        }
        adapter = new ListAdapter(MainActivity.this, R.layout.item_main, listItems);
    }

    private void initAlarm() {
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("com.meiying.alarm");
        intent.putExtra("msg", "check credit");
        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                60 * 1000, sender);
    }

    private void initUI() {
        setTitleRight("简单记", "选择月份");
        tvYear = (TextView) findViewById(R.id.tv_year);
        tvMonth = (TextView) findViewById(R.id.tv_month);
        tvIncome = (TextView) findViewById(R.id.tv_income);
        tvPay = (TextView) findViewById(R.id.tv_pay);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvNumber = (TextView) findViewById(R.id.tv_number);
        tvPersonalCenter = (TextView) findViewById(R.id.tv_personal_center);
        //下划线处理
        tvIncome.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvPay.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvNumber.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        //数据设置
        tvYear.setText(selectMonth.split("年")[0]);
        tvMonth.setText(selectMonth.split("年")[1].split("月")[0]);
        tvIncome.setText("收  ￥" + String.valueOf(inMoney));
        tvPay.setText("支  ￥" + String.valueOf(outMoney));
        String total = String.valueOf(totalMoney);
        tvNumber.setText(total.contains("-") ? total.replace("-", "￥") : ("￥" + total));
        if (totalMoney >= 0) {
            tvStatus.setText("结余");
            tvStatus.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.green_008b00));
            tvNumber.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.green_008b00));
        } else {
            tvStatus.setText("透支");
            tvStatus.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red_ff6347));
            tvNumber.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red_ff6347));
        }

        ListView content_list = (ListView) findViewById(R.id.content_list);
        content_list.setAdapter(adapter);
        content_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int itemIndex, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("确认删除?")
                        .setTitle("提示")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String id = (String)listItems.get(itemIndex).get("_id");
                                final String objectId = SQLiteUtils.getObjectIdFromId(id);
                                SQLiteUtils.deleteRecord(id);
                                record_table record = new record_table();
                                record.delete(objectId, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            SQLiteUtils.uploadRecord(objectId);
                                            Log.e(TAG, "delete");
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                listItems.remove(itemIndex);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create().show();
                return true;
            }
        });
    }

    private void bindEvents() {
        findViewById(R.id.acitivity_make_a_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.analyse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AnalyseActivity.class);
                intent.putExtra("current", selectMonth);
                startActivity(intent);
            }
        });
        findViewById(R.id.title_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, DatePickerDialog.THEME_HOLO_LIGHT, dateSetListener, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        initData();
                        initUI();
                    }
                });

                DatePicker dp = findDatePicker((ViewGroup) datePickerDialog.getWindow().getDecorView());
                if (dp != null)
                    ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
            }
        });
        tvPersonalCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PersonalCenterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getLocalData(String currentDate) {
        if (currentDate.equals(""))
            getDate();
        dataFromDb = SQLiteUtils.getRecords(currentDate);
    }

    private DatePicker findDatePicker(ViewGroup group) {
        if (group != null) {
            for (int i = 0, j = group.getChildCount(); i < j; i++) {
                View child = group.getChildAt(i);
                if (child instanceof DatePicker)
                    return (DatePicker) child;
                else if (child instanceof ViewGroup) {
                    DatePicker result = findDatePicker((ViewGroup) child);
                    if (result != null)
                        return result;
                }
            }
        }
        return null;
    }

    private void getTotalMoney() {
        double number;
        inMoney = 0;
        outMoney = 0;
        totalMoney = 0;
        for (Map<String, Object> map : listItems) {
            String money = (String) map.get("money");
            number = Double.parseDouble(money.substring(1, money.length()));
            if (money.contains("-")) {
                outMoney += number;
            } else if (money.contains("+")) {
                inMoney += number;
            }
        }
        totalMoney = inMoney - outMoney;
    }

    private void getDate() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectMonth = mYear + "年" + DateUtil.dateDeal(mMonth + 1) + "月";
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            mYear = i;
            mMonth = i1;
            mDay = i2;
            selectMonth = mYear + "年" + DateUtil.dateDeal(mMonth + 1) + "月";
        }
    };
}

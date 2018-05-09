package com.meiyin.moneyrecorder.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.meiyin.moneyrecorder.BaseActivity;
import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.adapter.ListAdapter;
import com.meiyin.moneyrecorder.entities.ListItem;
import com.meiyin.moneyrecorder.entities.RecordItems;
import com.meiyin.moneyrecorder.sqlite.SQLiteUtils;
import com.meiyin.moneyrecorder.utils.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cootek332 on 18/4/1.
 */

public class MainActivity extends BaseActivity {

    ArrayList<RecordItems> dataFromDb;
    //    SimpleAdapter adapter;
    ListAdapter adapter;
    List<String> monthsList;
    Map<Integer, String> monthsMap;
    static List<Map<String, Object>> listItems;
    List<Map<String, Object>> reverseList;
    String currentMonth = "";

    private TextView tvYear, tvMonth, tvIncome, tvPay, tvStatus, tvNumber;

    private double inMoney, outMoney, totalMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteUtils.init();
        initData();
        initUI();
        bindEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initUI();
    }

    private void initData() {
        getLocalData();
        getMonths();
        initItems();
        getTotalMoney();
    }

    private void getMonths() {
        if (monthsMap == null) {
            monthsMap = new HashMap<>();
        }
        if (monthsList == null) {
            monthsList = new ArrayList<>();
        }
        monthsMap.clear();
        monthsList.clear();
        for (int i = 0; i < dataFromDb.size(); i++) {
            String month = dataFromDb.get(i).getSetTime().split("月")[0];
            monthsMap.put(i, month);
            if (!monthsList.contains(month)) {
                monthsList.add(month);
            }
        }
        Collections.sort(monthsList, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                int sYear = Integer.parseInt(s.split("年")[0]);
                int sMonth = Integer.parseInt(s.split("年")[1].split("月")[0]);
                int t1Year = Integer.parseInt(t1.split("年")[0]);
                int t1Month = Integer.parseInt(t1.split("年")[1].split("月")[0]);
                int yearCompare = t1Year - sYear;
                int monthCompare = t1Month - sMonth;
                return yearCompare == 0 ? monthCompare : yearCompare;
            }
        });
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
        if ("".equals(currentMonth) && monthsList != null && monthsList.size() > 0) {
            currentMonth = monthsList.get(0) + "月";
        }
        if (listItems == null) {
            listItems = new ArrayList<>();
        }
        listItems.clear();
        for (int i = 0; i < dataFromDb.size(); i++) {
            if (!currentMonth.equals(dataFromDb.get(i).getSetTime().split("月")[0] + "月")) {
                continue;
            }
            Map<String, Object> listItem = new HashMap<>();
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
//        adapter = new SimpleAdapter(this, listItems,
//                R.layout.item_list,
//                new String[]{"buyClassifyOne", "payClassify", "money", "setTime"},
//                new int[]{R.id.item_buyDataClassifyOne, R.id.item_payClassify, R.id.item_money, R.id.item_time});

        reverseList = new ArrayList<>();
        for (int i = listItems.size() - 1; i >= 0; i--) {
            reverseList.add(listItems.get(i));
        }
        adapter = new ListAdapter(MainActivity.this, R.layout.item_main, reverseList);
    }

    private void initUI() {
        setTitleRight("简单记", "选择月份");
        setTitleBackground(R.color.colorMain);
        tvYear = (TextView) findViewById(R.id.tv_year);
        tvMonth = (TextView) findViewById(R.id.tv_month);
        tvIncome = (TextView) findViewById(R.id.tv_income);
        tvPay = (TextView) findViewById(R.id.tv_pay);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvNumber = (TextView) findViewById(R.id.tv_number);

        //下划线处理
        tvIncome.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvPay.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvNumber.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        //数据设置
        String[] time = DateUtil.getCurrentTime().split("-");
        tvYear.setText(time[0]);
        tvMonth.setText(time[1]);
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
                                SQLiteUtils.delete((String) listItems.get(listItems.size() - itemIndex - 1).get("_id"));
                                listItems.remove(listItems.size() - itemIndex - 1);
                                adapter.notifyDataSetChanged();
                                if (listItems.size() == 0) {
                                    currentMonth = "";
                                }
                                initData();
                                initUI();
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
                intent.putExtra("title", currentMonth);
                startActivity(intent);
            }
        });
        findViewById(R.id.title_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Wait for new", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocalData() {
        dataFromDb = SQLiteUtils.getRecords();
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

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            currentMonth = ((TextView) view).getText().toString();
            initItems();
            initUI();
        }
    };
}

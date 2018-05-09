package com.meiyin.moneyrecorder.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.entities.RecordItems;
import com.meiyin.moneyrecorder.sqlite.SQLiteUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cootek332 on 18/4/1.
 */

public class MainActivity extends Activity {
    public static Activity mActivity;
    ArrayList<RecordItems> dataFromDb;
    SimpleAdapter adapter;
    List<String> monthsList;
    Map<Integer, String> monthsMap;
    static List<Map<String, Object>> listItems;
    LinearLayout main_months_scroll;
    String currentMonth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteUtils.init();
        initData();
        initUI();
        bindEvents();
        mActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocalData();
        getMonths();
        initItems();
        initUI();
    }

    private void initData() {
        getLocalData();
        getMonths();
        initItems();
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
        if ("".equals(currentMonth)
                && monthsList != null
                && monthsList.size() > 0 ) {
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
            listItem.put("_id", dataFromDb.get(i).getId().toString());
            listItem.put("buyClassifyOne", dataFromDb.get(i).getBuyClassifyOne().toString());
            listItem.put("payClassify", dataFromDb.get(i).getPayClassify());
            String money = dataFromDb.get(i).getMoney() + "";
            if (!money.contains("-")) {
                money = "+" + money;
            }
            listItem.put("money", money);
            listItem.put("setTime", dataFromDb.get(i).getSetTime().split("年")[1]);
            listItems.add(listItem);
        }
        adapter = new SimpleAdapter(this, listItems,
                R.layout.item_list,
                new String[]{"buyClassifyOne", "payClassify", "money", "setTime"},
                new int[]{R.id.item_buyDataClassifyOne, R.id.item_payClassify, R.id.item_money, R.id.item_time});
    }

    private void initUI() {
        main_months_scroll = (LinearLayout)findViewById(R.id.main_months_scroll);
        main_months_scroll.removeAllViews();
        for (int i = monthsList.size() - 1; i > -1 ; i--) {
            TextView monthTv = new TextView(this);
            monthTv.setText(monthsList.get(i) + "月");
            monthTv.setGravity(View.TEXT_ALIGNMENT_CENTER);
            monthTv.setPadding(100, 10, 100, 10);
            monthTv.setOnClickListener(mOnClickListener);
            main_months_scroll.addView(monthTv);
        }
        ListView content_list = (ListView)findViewById(R.id.content_list);
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
                                SQLiteUtils.delete((String)listItems.get(itemIndex).get("_id"));
                                listItems.remove(itemIndex);
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
        findViewById(R.id.activity_percenal_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PersonalCenterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getLocalData() {
        dataFromDb = SQLiteUtils.getRecords();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            currentMonth = ((TextView)view).getText().toString();
            initItems();
            initUI();
        }
    };
}

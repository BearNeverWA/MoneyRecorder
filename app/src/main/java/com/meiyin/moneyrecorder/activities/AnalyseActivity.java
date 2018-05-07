package com.meiyin.moneyrecorder.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.LauncherActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meiyin.moneyrecorder.Fragments.IncomeFragment;
import com.meiyin.moneyrecorder.Fragments.PayFragment;
import com.meiyin.moneyrecorder.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by cootek332 on 18/4/1.
 */

public class AnalyseActivity extends Activity {
    List<Map<String, Object>> listItems;
    //定义总饼图的Map数据
    Map<String, Double> analysedMap;
    //定义总饼图的view
    private PieChartView chart;
    //定义总饼图的数据
    private PieChartData chartData;
    //定义支出饼图的数据
    Map<String, Double> analysedPayMap;
    private PieChartView payChart;
    private PieChartData payChartData;
    //定义收入饼图的数据
    Map<String, Double> analysedIncomeMap;
    private PieChartView incomeChart;
    private PieChartData incomeChartData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);

        chart = (PieChartView)findViewById(R.id.pie_chart);
        payChart = (PieChartView)findViewById(R.id.pie_chart_pay);
        incomeChart = (PieChartView)findViewById(R.id.pie_chart_income);

        initData();
        initUI();
        bindEvents();
    }

    private void initData() {
        listItems = MainActivity.listItems;
        if (listItems == null || listItems.size() == 0) {
            Toast.makeText(AnalyseActivity.this, "数据不合法,请重新选择月份", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (analysedMap != null) {
            analysedMap.clear();
        } else {
            analysedMap = new HashMap<>();
        }
        if (analysedPayMap != null) {
            analysedPayMap.clear();
        } else {
            analysedPayMap = new HashMap<>();
        }
        if (analysedIncomeMap != null) {
            analysedIncomeMap.clear();
        } else {
            analysedIncomeMap = new HashMap<>();
        }
        for (int i = 0; i < listItems.size(); i++) {
            String itemType = listItems.get(i).get("buyClassifyOne").toString();
            double itemNumber =  Double.parseDouble(listItems.get(i).get("money").toString());
            if (analysedMap.containsKey(itemType)) {
                analysedMap.put(itemType, itemNumber + analysedMap.get(itemType));
            } else {
                analysedMap.put(itemType, itemNumber);
            }
            if (itemNumber < 0 ) {
                if (analysedPayMap.containsKey(itemType)) {
                    analysedPayMap.put(itemType, itemNumber + analysedPayMap.get(itemType));
                } else {
                    analysedPayMap.put(itemType, itemNumber);
                }
            } else if (itemNumber > 0) {
                if (analysedIncomeMap.containsKey(itemType)) {
                    analysedIncomeMap.put(itemType, itemNumber + analysedIncomeMap.get(itemType));
                } else {
                    analysedIncomeMap.put(itemType, itemNumber);
                }
            }
        }

        //data for pie
        List<SliceValue> values = new ArrayList<>();
        if (analysedMap.size() <= 0) {
            chart.setVisibility(View.GONE);
        }
        for (String key : analysedMap.keySet()) {
            float number = Float.parseFloat(analysedMap.get(key).toString());
            if (number == 0f) {
                continue;
            }
            SliceValue sValue = new SliceValue(number, ChartUtils.pickColor());
            sValue.setLabel(key + ":" + number);
            values.add(sValue);
        }
        chartData = new PieChartData(values);
        chartData.setHasLabels(true);
        chartData.setValueLabelTextSize(8);
        chartData.setHasCenterCircle(true);
        chartData.setCenterText1("总计");
        chartData.setCenterText1FontSize(20);

        //data for pay pie
        List<SliceValue> values_pay = new ArrayList<>();
        if (analysedPayMap.size() <= 0) {
            payChart.setVisibility(View.GONE);
        }
        for (String key : analysedPayMap.keySet()) {
            float number = Float.parseFloat(analysedPayMap.get(key).toString());
            if (number == 0f) {
                continue;
            }
            SliceValue sValue = new SliceValue(number, ChartUtils.pickColor());
            sValue.setLabel(key + ":" + number);
            values_pay.add(sValue);
        }
        payChartData = new PieChartData(values_pay);
        payChartData.setHasLabels(true);
        payChartData.setValueLabelTextSize(8);
        payChartData.setHasCenterCircle(true);
        payChartData.setCenterText1("支出");
        payChartData.setCenterText1FontSize(20);

        //data for income pie
        List<SliceValue> values_income = new ArrayList<>();
        if (analysedIncomeMap.size() <= 0) {
            incomeChart.setVisibility(View.GONE);
        }
        for (String key : analysedIncomeMap.keySet()) {
            float number = Float.parseFloat(analysedIncomeMap.get(key).toString());
            if (number == 0f) {
                continue;
            }
            SliceValue sValue = new SliceValue(number, ChartUtils.pickColor());
            sValue.setLabel(key + ":" + number);
            values_income.add(sValue);
        }
        incomeChartData = new PieChartData(values_income);
        incomeChartData.setHasLabels(true);
        incomeChartData.setValueLabelTextSize(8);
        incomeChartData.setHasCenterCircle(true);
        incomeChartData.setCenterText1("收入");
        incomeChartData.setCenterText1FontSize(20);
    }

    private void initUI() {
        String title = getIntent().getExtras().getString("title");
        ((TextView)findViewById(R.id.analyse_title)).setText(title + "分析");
        chart.setPieChartData(chartData);
        payChart.setPieChartData(payChartData);
        incomeChart.setPieChartData(incomeChartData);
    }

    private void bindEvents() {
    }
}

package com.meiyin.moneyrecorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.meiyin.moneyrecorder.BaseActivity;
import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.utils.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by cootek332 on 18/4/1.
 */

public class AnalyseActivity extends BaseActivity {
    List<Map<String, Object>> listItems;
    Map<String, Double> inMap, outMap;

    private PieChart incomeChart, payChart;
    private RelativeLayout inLayout, outLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);

        payChart = findViewById(R.id.pie_chart_pay);
        incomeChart = findViewById(R.id.pie_chart_income);
        inLayout = findViewById(R.id.in_layout);
        outLayout = findViewById(R.id.out_layout);

        initUI();
        bindEvents();
    }

    private void initData() {
        listItems = MainActivity.listItems;
        if (listItems == null || listItems.size() == 0) {
            Toast.makeText(AnalyseActivity.this, "数据不合法,请重新选择月份", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (inMap != null)
            inMap.clear();
        else
            inMap = new HashMap<>();
        if (outMap != null)
            outMap.clear();
        else
            outMap = new HashMap<>();

        for (int i = 0; i < listItems.size(); i++) {
            String type = listItems.get(i).get("buyClassifyOne").toString();
            double number = Double.parseDouble(listItems.get(i).get("money").toString());
            if (number < 0) {
                if (outMap.containsKey(type))
                    outMap.put(type, number + outMap.get(type));
                else
                    outMap.put(type, number);
            } else if (number > 0) {
                if (inMap.containsKey(type))
                    inMap.put(type, number + inMap.get(type));
                else
                    inMap.put(type, number);
            }
        }

        //收入数据
        if (inMap.size() <= 0)
            inLayout.setVisibility(View.GONE);
        else {
            inLayout.setVisibility(View.VISIBLE);

            List<String> keyInlist = new ArrayList<>();
            List<Entry> inList = new ArrayList<>();
            int i = 0;
            for (String key : inMap.keySet()) {
                Float value = Float.parseFloat(inMap.get(key).toString());
                if (value == 0f)
                    continue;
                else {
                    keyInlist.add(key);
                    inList.add(new Entry(value, i++));
                }
            }
            PieDataSet inDataSet = new PieDataSet(inList, null);
            //color
            ArrayList<Integer> colors = new ArrayList<>();
            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);
            colors.add(ColorTemplate.getHoloBlue());
            inDataSet.setColors(colors);
            inDataSet.setSliceSpace(2f);
            inDataSet.setSelectionShift(5f);

            PieData inData = new PieData(keyInlist, inDataSet);
            inData.setValueTextSize(10);
            incomeChart.setData(inData);
        }

        //支出数据
        if (outMap.size() <= 0)
            outLayout.setVisibility(View.GONE);
        else {
            outLayout.setVisibility(View.VISIBLE);

            List<String> keyOutlist = new ArrayList<>();
            List<Entry> outList = new ArrayList<>();
            int j = 0;
            for (String key : outMap.keySet()) {
                Float value = Float.parseFloat(outMap.get(key).toString());
                if (value == 0f)
                    continue;
                else {
                    keyOutlist.add(key);
                    outList.add(new Entry(Math.abs(value), j++));
                }
            }
            PieDataSet outDataSet = new PieDataSet(outList, null);
            //color
            ArrayList<Integer> colors = new ArrayList<>();
            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);
            colors.add(ColorTemplate.getHoloBlue());
            outDataSet.setColors(colors);
            outDataSet.setSliceSpace(2f);
            outDataSet.setSelectionShift(5f);

            PieData outData = new PieData(keyOutlist, outDataSet);
            outData.setValueTextSize(10);
            payChart.setData(outData);
        }
    }

    private void initUI() {
        Intent intent = getIntent();
        String strIntent = intent.getStringExtra("current");
        if (strIntent != null && strIntent.length() != 0)
            setTitleLeft(strIntent, "返回");
        else {
            String[] data = DateUtil.getCurrentYearMonth().split("-");
            setTitleLeft(data[0] + "年" + data[1] + "月", "返回");
        }

        initData();

        //income
        incomeChart.setUsePercentValues(true);
        incomeChart.setDescription("");
        incomeChart.setDrawCenterText(true);
        incomeChart.setCenterText("收入");
        incomeChart.setNoDataText("数据好像被吃掉了哟");
        incomeChart.setRotationEnabled(true);
        incomeChart.setHighlightPerTapEnabled(true);
        incomeChart.setRotationAngle(270);
        incomeChart.animateXY(1500, 1500);
        incomeChart.setCenterTextSize(16);

        //out
        payChart.setUsePercentValues(true);
        payChart.setDescription("");
        payChart.setDrawCenterText(true);
        payChart.setCenterText("支出");
        payChart.setNoDataText("数据好像被吃掉了哟");
        payChart.setRotationEnabled(true);
        payChart.setHighlightPerTapEnabled(true);
        payChart.setRotationAngle(90);
        payChart.animateXY(1500, 1500);
        payChart.setCenterTextSize(16);
    }

    private void bindEvents() {
        findViewById(R.id.title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

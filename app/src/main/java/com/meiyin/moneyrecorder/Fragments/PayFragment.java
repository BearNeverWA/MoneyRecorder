package com.meiyin.moneyrecorder.Fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.entities.RecordItems;
import com.meiyin.moneyrecorder.sqlite.SQLiteUtils;

import java.util.Calendar;

/**
 * Created by cootek332 on 18/4/1.
 */

public class PayFragment extends Fragment {
    private static final String[] classifySpinner = {"购物", "零食", "交通", "住房", "餐饮", "娱乐", "社交", "话费", "借出", "其它"};
    private static final String[] payClassifySpinner = {"支付宝", "微信", "招商银行", "交通银行"};
    View view;
    TextView dateView;

    int mYear;
    int mMonth;
    int mDay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_pay, container, false);
        initDate();
        initUI();
        bindEvents();
        return view;
    }

    private void initDate() {
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
    }

    private void initUI() {
        dateView = (TextView) view.findViewById(R.id.pay_date_picker);

        ArrayAdapter classifyAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, classifySpinner);
        ArrayAdapter payClassifyAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, payClassifySpinner);

        Spinner classifySpinner = (Spinner)view.findViewById(R.id.classify_spinner);
        Spinner payClassifySpinner = (Spinner)view.findViewById(R.id.pay_classify_spinner);

        classifySpinner.setAdapter(classifyAdapter);
        payClassifySpinner.setAdapter(payClassifyAdapter);
    }

    private void bindEvents() {
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), onDateSetListener, mYear, mMonth, mDay).show();
            }
        });
        view.findViewById(R.id.pay_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner buyClassifyOne = (Spinner)view.findViewById(R.id.classify_spinner);
                Spinner payClassify = (Spinner)view.findViewById(R.id.pay_classify_spinner);

                EditText moneyET = (EditText)view.findViewById(R.id.pay_money);
                TextView dateTV = (TextView)view.findViewById(R.id.pay_date_picker);
                if (TextUtils.isEmpty(moneyET.getText().toString())) {
                    Toast.makeText(getActivity(), "请输入金额", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(dateTV.getText().toString())) {
                    Toast.makeText(getActivity(), "请选择日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                SQLiteUtils.insertRecord(new RecordItems(null, buyClassifyOne.getSelectedItem().toString(),
                        payClassify.getSelectedItem().toString(),
                        Double.parseDouble("-" + moneyET.getText().toString()),
                        dateTV.getText().toString(),
                        Calendar.getInstance().getTimeInMillis(), 0));
                Toast.makeText(getActivity(), "数据保存成功", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });

    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String days;
            if (mMonth + 1 < 10) {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("年").append("0").
                            append(mMonth + 1).append("月").append("0").append(mDay).append("日").toString();
                } else {
                    days = new StringBuffer().append(mYear).append("年").append("0").
                            append(mMonth + 1).append("月").append(mDay).append("日").toString();
                }

            } else {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("年").
                            append(mMonth + 1).append("月").append("0").append(mDay).append("日").toString();
                } else {
                    days = new StringBuffer().append(mYear).append("年").
                            append(mMonth + 1).append("月").append(mDay).append("日").toString();
                }

            }
            dateView.setText(days);
        }
    };
}
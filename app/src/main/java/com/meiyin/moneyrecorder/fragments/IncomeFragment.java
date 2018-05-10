package com.meiyin.moneyrecorder.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.adapter.ClassAdapter;
import com.meiyin.moneyrecorder.entities.RecordItems;
import com.meiyin.moneyrecorder.entities.RvItem;
import com.meiyin.moneyrecorder.sqlite.SQLiteUtils;
import com.meiyin.moneyrecorder.utils.CommonUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by cootek332 on 18/4/1.
 */

public class IncomeFragment extends Fragment {

    View view;
    View popupView;
    private PopupWindow popupWindow;
    private TextView dateView;
    private TextView tvSelected, incomeClass;
    private ImageView ivSelected;
    private EditText moneyET;
    private RecyclerView recyclerView;
    private ClassAdapter adapter;

    private List<RvItem> rvItemList;

    int mYear;
    int mMonth;
    int mDay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_income, container, false);
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

        initListData();
    }

    private void initUI() {
        tvSelected = (TextView) view.findViewById(R.id.tv_selected_income);
        ivSelected = (ImageView) view.findViewById(R.id.iv_selected_income);
        dateView = (TextView) view.findViewById(R.id.income_date_picker);
        dateView.setText(mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_classify_income);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 5);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ClassAdapter(rvItemList);
        adapter.setmOnItemClickListener(new ClassAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ivSelected.setImageResource(rvItemList.get(position).getResId());
                tvSelected.setText(rvItemList.get(position).getName());
            }
        });
        recyclerView.setAdapter(adapter);

        incomeClass = (TextView) view.findViewById(R.id.income_classify_select);
        incomeClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow.isShowing())
                    dismissPop();
                else
                    showPop();
            }
        });

        //popup
        initPop();
        popupView.findViewById(R.id.pay_class_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeClass.setText("微信");
                dismissPop();
            }
        });
        popupView.findViewById(R.id.pay_class_alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeClass.setText("支付宝");
                dismissPop();
            }
        });
        popupView.findViewById(R.id.pay_class_bank_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeClass.setText("银行卡");
                dismissPop();
            }
        });
        popupView.findViewById(R.id.pay_class_meal_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeClass.setText("饭卡");
                dismissPop();
            }
        });
        popupView.findViewById(R.id.pay_class_traffic_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeClass.setText("交通卡");
                dismissPop();
            }
        });
    }

    private void bindEvents() {
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), onDateSetListener, mYear, mMonth, mDay).show();
            }
        });
        view.findViewById(R.id.income_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyET = (EditText) view.findViewById(R.id.income_money);
                if (TextUtils.isEmpty(moneyET.getText().toString())) {
                    Toast.makeText(getActivity(), "请输入金额", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(dateView.getText().toString())) {
                    Toast.makeText(getActivity(), "请选择日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                SQLiteUtils.insertRecord(new RecordItems(null, tvSelected.getText().toString(),
                        incomeClass.getText().toString(),
                        Double.parseDouble(moneyET.getText().toString()),
                        dateView.getText().toString(),
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

    private void initListData() {
        rvItemList = new ArrayList<>();
        RvItem salary = new RvItem();
        salary.setResId(R.mipmap.salary);
        salary.setName("工资");
        rvItemList.add(salary);

        RvItem redbag = new RvItem();
        redbag.setResId(R.mipmap.in_redbag);
        redbag.setName("红包");
        rvItemList.add(redbag);

        RvItem bonus = new RvItem();
        bonus.setResId(R.mipmap.bonus);
        bonus.setName("奖金");
        rvItemList.add(bonus);

        RvItem financing = new RvItem();
        financing.setResId(R.mipmap.financing);
        financing.setName("理财");
        rvItemList.add(financing);

        RvItem partTime = new RvItem();
        partTime.setResId(R.mipmap.part_time);
        partTime.setName("兼职");
        rvItemList.add(partTime);

        RvItem allowance = new RvItem();
        allowance.setResId(R.mipmap.allowance);
        allowance.setName("补助");
        rvItemList.add(allowance);

        RvItem reimbursement = new RvItem();
        reimbursement.setResId(R.mipmap.reimbursement);
        reimbursement.setName("报销");
        rvItemList.add(reimbursement);

        RvItem refund = new RvItem();
        refund.setResId(R.mipmap.refund);
        refund.setName("退款");
        rvItemList.add(refund);
    }

    private void initPop() {
        popupView = getActivity().getLayoutInflater().inflate(R.layout.popup_pay_class, null, false);
        popupWindow = new PopupWindow(popupView, (int) CommonUtil.dip2px(80), ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }

    private void dismissPop() {
        if (popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();
    }

    private void showPop() {
        if (popupWindow == null)
            initPop();
        if (!popupWindow.isShowing())
            popupWindow.showAsDropDown(incomeClass, 0, -40);
    }
}

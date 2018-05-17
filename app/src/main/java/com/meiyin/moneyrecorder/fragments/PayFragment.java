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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.adapter.ClassAdapter;
import com.meiyin.moneyrecorder.entities.RecordItems;
import com.meiyin.moneyrecorder.entities.RvItem;
import com.meiyin.moneyrecorder.entities.record_table;
import com.meiyin.moneyrecorder.sqlite.SQLiteUtils;
import com.meiyin.moneyrecorder.utils.CommonUtil;
import com.meiyin.moneyrecorder.utils.DateUtil;
import com.meiyin.moneyrecorder.utils.SharePreferenceKeys;
import com.meiyin.moneyrecorder.utils.SharePreferenceUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by cootek332 on 18/4/1.
 */

public class PayFragment extends Fragment {
    View view;
    View popupView;
    PopupWindow popupWindow;
    private TextView dateView;
    private TextView tvSelected, payClass;
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
        view = inflater.inflate(R.layout.fragmetn_pay, container, false);
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
        tvSelected = view.findViewById(R.id.tv_selected);
        ivSelected = view.findViewById(R.id.iv_selected);
        dateView = view.findViewById(R.id.pay_date_picker);
        dateView.setText(mYear + "年" + DateUtil.dateDeal(mMonth + 1) + "月" + DateUtil.dateDeal(mDay) + "日");
        recyclerView = view.findViewById(R.id.rv_classify);
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

        payClass = view.findViewById(R.id.pay_classify_select);
        payClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow.isShowing())
                    dismissPop();
                else
                    showPop();
            }
        });

        //PopupWindow
        initPop();
        popupView.findViewById(R.id.pay_class_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payClass.setText("微信");
                dismissPop();
            }
        });
        popupView.findViewById(R.id.pay_class_alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payClass.setText("支付宝");
                dismissPop();
            }
        });
        popupView.findViewById(R.id.pay_class_bank_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payClass.setText("银行卡");
                dismissPop();
            }
        });
        popupView.findViewById(R.id.pay_class_meal_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payClass.setText("饭卡");
                dismissPop();
            }
        });
        popupView.findViewById(R.id.pay_class_traffic_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payClass.setText("交通卡");
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
        view.findViewById(R.id.pay_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyET = view.findViewById(R.id.pay_money);
                if (TextUtils.isEmpty(moneyET.getText().toString())) {
                    Toast.makeText(getActivity(), "请输入金额", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(dateView.getText().toString())) {
                    Toast.makeText(getActivity(), "请选择日期", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String classify = payClass.getText().toString();
                final record_table record = new record_table();
                record.setrMoney(Double.parseDouble(moneyET.getText().toString()));
                record.setsUserName(SharePreferenceUtil.getStringRecord(SharePreferenceKeys.KEY_USER_NAME));
                record.setiDeleted(0);
                record.setiUploaded(0);
                record.setsPayClassify(classify);
                record.setiCurrentTime(Calendar.getInstance().getTimeInMillis() + "");
                record.setsTime(dateView.getText().toString());
                record.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        int uploaded = e == null ? 1 : 0;
                        if (e != null) {
                            e.printStackTrace();
                        }
                        SQLiteUtils.insertRecord(new RecordItems(s, null, tvSelected.getText().toString(),
                                payClass.getText().toString(),
                                Double.parseDouble("-" + moneyET.getText().toString()),
                                dateView.getText().toString(),
                                Calendar.getInstance().getTimeInMillis(), 0, uploaded));
                    }
                });
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
            String days = mYear + "年" + DateUtil.dateDeal(mMonth + 1) + "月" + DateUtil.dateDeal(mDay) + "日";
            dateView.setText(days);
        }
    };

    private void initListData() {
        rvItemList = new ArrayList<>();
        RvItem eatDrink = new RvItem();
        eatDrink.setResId(R.mipmap.eat_drink_play_happy);
        eatDrink.setName("吃喝");
        rvItemList.add(eatDrink);

        RvItem traffic = new RvItem();
        traffic.setResId(R.mipmap.traffic);
        traffic.setName("交通");
        rvItemList.add(traffic);

        RvItem shopping = new RvItem();
        shopping.setResId(R.mipmap.shopping);
        shopping.setName("购物");
        rvItemList.add(shopping);

        RvItem cloth = new RvItem();
        cloth.setResId(R.mipmap.clothes);
        cloth.setName("服饰");
        rvItemList.add(cloth);

        RvItem rvItem = new RvItem();
        rvItem.setResId(R.mipmap.entertainmnet);
        rvItem.setName("娱乐");
        rvItemList.add(rvItem);

        RvItem redBag = new RvItem();
        redBag.setResId(R.mipmap.redbag);
        redBag.setName("红包");
        rvItemList.add(redBag);

        RvItem waterPower = new RvItem();
        waterPower.setResId(R.mipmap.water_power);
        waterPower.setName("水电");
        rvItemList.add(waterPower);

        RvItem smokeWine = new RvItem();
        smokeWine.setResId(R.mipmap.cigarette_wine_salt);
        smokeWine.setName("烟酒");
        rvItemList.add(smokeWine);

        RvItem fruit = new RvItem();
        fruit.setResId(R.mipmap.fruit);
        fruit.setName("水果");
        rvItemList.add(fruit);

        RvItem daily = new RvItem();
        daily.setResId(R.mipmap.necessities);
        daily.setName("日用品");
        rvItemList.add(daily);
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
            popupWindow.showAsDropDown(payClass, 0, -40);
    }
}

package com.meiyin.moneyrecorder.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.entities.CreditItems;
import com.meiyin.moneyrecorder.sqlite.SQLiteUtils;
import com.meiyin.moneyrecorder.utils.SharePreferenceKeys;
import com.meiyin.moneyrecorder.utils.SharePreferenceUtil;

import java.util.ArrayList;

/**
 * Created by cootek332 on 18/5/9.
 */

public class PersonalCenterActivity extends Activity {
    public static Activity mActivity;

    private TextView account_tv;
    private TextView exit_tv;
    private LinearLayout credit_ll;
    private Button add_credit_btn;
    private LinearLayout fill_credit_info_ll;
    private Button confirm_credit_info_btn;
    private Button cancel_credit_info_btn;
    private EditText bank_name_et;
    private EditText card_number_et;
    private EditText bill_day_et;
    private EditText pay_day_et;
    private LinearLayout credit_warning_ll;
    private Button credit_warning_ok;
    private Button credit_warning_cancel;

    private int pay_date_from_receiver = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        mActivity = this;
        account_tv = (TextView) findViewById(R.id.personal_account);
        exit_tv = (TextView)findViewById(R.id.personal_exit);
        credit_ll = (LinearLayout)findViewById(R.id.creditcard_ll);
        add_credit_btn = (Button)findViewById(R.id.add_credit_btn);
        fill_credit_info_ll = (LinearLayout)findViewById(R.id.fill_credit_info_ll);
        confirm_credit_info_btn = (Button) findViewById(R.id.confirm_credit_info_btn);
        cancel_credit_info_btn = (Button) findViewById(R.id.cancel_credit_info_btn);
        bank_name_et = (EditText)findViewById(R.id.bank_name);
        card_number_et = (EditText)findViewById(R.id.card_number);
        bill_day_et = (EditText)findViewById(R.id.bill_day);
        pay_day_et = (EditText)findViewById(R.id.pay_day);
        credit_warning_ll = (LinearLayout)findViewById(R.id.credit_warning_ll);
        credit_warning_ok = (Button)findViewById(R.id.credit_warning_ok);
        credit_warning_cancel = (Button)findViewById(R.id.credit_warning_cancel);
        initUI();
        bindEvents();
        fetchAndShowCredit();
    }
    private void initUI() {
        account_tv.setText(SharePreferenceUtil.getStringRecord(SharePreferenceKeys.KEY_USER_NAME));
        fill_credit_info_ll.setClickable(true);
        pay_date_from_receiver = getIntent().getIntExtra("card", -1);
        if (pay_date_from_receiver == 0 || pay_date_from_receiver ==1) {
            credit_warning_ll.setVisibility(View.VISIBLE);
        }
    }

    private void bindEvents() {
        exit_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalCenterActivity.this);
                builder.setTitle("【提示】");
                builder.setMessage("退出账户将清除所有的本地数据,确认退出?");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharePreferenceUtil.setRecord(SharePreferenceKeys.KEY_USER_NAME, "");
                        SharePreferenceUtil.setRecord(SharePreferenceKeys.KEY_PASSWORD, "");
                        SQLiteUtils.clearAll();
                        Intent intent = new Intent(PersonalCenterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        if (MainActivity.mActivity != null) {
                            MainActivity.mActivity.finish();
                        }
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        confirm_credit_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bank = bank_name_et.getText().toString();
                String card_number = card_number_et.getText().toString();
                String bill_day = bill_day_et.getText().toString();
                String pay_day = pay_day_et.getText().toString();
                int bill_day_int = 0;
                int pay_day_int = 0;
                if (TextUtils.isEmpty(bank)) {
                    Toast.makeText(PersonalCenterActivity.this, "请填入银行名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(card_number)) {
                    Toast.makeText(PersonalCenterActivity.this, "请填入银行卡号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(bill_day)) {
                    Toast.makeText(PersonalCenterActivity.this, "请填入出账日", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pay_day)) {
                    Toast.makeText(PersonalCenterActivity.this, "请填入还款日", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    bill_day_int = Integer.parseInt(bill_day);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PersonalCenterActivity.this, "出账日格式错误,请填入1-28之间的数字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (bill_day_int > 28 || bill_day_int < 1 ) {
                    Toast.makeText(PersonalCenterActivity.this, "出账日格式错误,请填入1-28之间的数字", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    pay_day_int = Integer.parseInt(pay_day);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PersonalCenterActivity.this, "还款日格式错误,请填入1-28之间的数字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pay_day_int > 28 || pay_day_int < 1 ) {
                    Toast.makeText(PersonalCenterActivity.this, "还款日格式错误,请填入1-28之间的数字", Toast.LENGTH_SHORT).show();
                    return;
                }
                recordCredit(bank, card_number, Integer.parseInt(bill_day), Integer.parseInt(pay_day));
                fetchAndShowCredit();
                clearTable();
            }
        });

        cancel_credit_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTable();
            }
        });

        add_credit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String record_days = SharePreferenceUtil.getStringRecord(SharePreferenceKeys.KEY_CREDIT_WARNING_DATES);
                if (record_days.split(" ").length >= 2) {
                    Toast.makeText(PersonalCenterActivity.this, "暂时只支持两张卡提醒", Toast.LENGTH_SHORT).show();
                    return;
                }
                fill_credit_info_ll.setVisibility(View.VISIBLE);
            }
        });

        credit_warning_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                credit_warning_ll.setVisibility(View.GONE);
                if (pay_date_from_receiver == 0) {
                    SharePreferenceUtil.setRecord(SharePreferenceKeys.KEY_WARNED_THIS_MONTH_CARD_1, true);
                } else if (pay_date_from_receiver == 1) {
                    SharePreferenceUtil.setRecord(SharePreferenceKeys.KEY_WARNED_THIS_MONTH_CARD_2, true);
                }
            }
        });

        credit_warning_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                credit_warning_ll.setVisibility(View.GONE);
            }
        });
    }

    private void recordCredit(String bank, String card_number, int bill_day, int pay_day) {
        CreditItems item = new CreditItems(null, bank, card_number, bill_day, pay_day, 0, 0);
        SQLiteUtils.insertCredit(item);
        String record_days = SharePreferenceUtil.getStringRecord(SharePreferenceKeys.KEY_CREDIT_WARNING_DATES);
        if (TextUtils.isEmpty(record_days)) {
            SharePreferenceUtil.setRecord(SharePreferenceKeys.KEY_CREDIT_WARNING_DATES, "" + pay_day);
        } else {
            SharePreferenceUtil.setRecord(SharePreferenceKeys.KEY_CREDIT_WARNING_DATES, record_days + " " + pay_day);
        }
    }

    private void fetchAndShowCredit() {
        credit_ll.removeAllViews();
        ArrayList<CreditItems> items = SQLiteUtils.getCredits();
        for (int i = 0; i < items.size(); i++) {
            CreditItems item = items.get(i);
            TextView tmp_tv = new TextView(PersonalCenterActivity.this);
            tmp_tv.setText(item.getBankName() + ": " + item.getCardNumber() + ", 出账日期: " + item.getBillDay() + ", 还款日期: " + item.getPayDay());
            tmp_tv.setTextSize(20);
            credit_ll.addView(tmp_tv);
        }
    }

    private void clearTable() {
        bank_name_et.setText("");
        card_number_et.setText("");
        bill_day_et.setText("");
        pay_day_et.setText("");
        fill_credit_info_ll.setVisibility(View.GONE);
    }
}

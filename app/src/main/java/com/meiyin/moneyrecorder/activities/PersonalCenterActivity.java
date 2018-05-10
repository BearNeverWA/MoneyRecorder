package com.meiyin.moneyrecorder.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.sqlite.SQLiteUtils;
import com.meiyin.moneyrecorder.utils.SharePreferenceKeys;
import com.meiyin.moneyrecorder.utils.SharePreferenceUtil;

/**
 * Created by cootek332 on 18/5/9.
 */

public class PersonalCenterActivity extends Activity {
    private TextView account_tv;
    private TextView exit_tv;
    private LinearLayout credit_ll;
    private Button add_credit_btn;
    private LinearLayout fill_credit_info_ll;
    private LinearLayout personal_main_ll;
    private Button confirm_credit_info_btn;
    private Button cancel_credit_info_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        account_tv = (TextView) findViewById(R.id.personal_account);
        exit_tv = (TextView)findViewById(R.id.personal_exit);
        credit_ll = (LinearLayout)findViewById(R.id.creditcard_ll);
        add_credit_btn = (Button)findViewById(R.id.add_credit_btn);
        fill_credit_info_ll = (LinearLayout)findViewById(R.id.fill_credit_info_ll);
        personal_main_ll = (LinearLayout)findViewById(R.id.personal_main_ll);
        confirm_credit_info_btn = (Button) findViewById(R.id.confirm_credit_info_btn);
        cancel_credit_info_btn = (Button) findViewById(R.id.cancel_credit_info_btn);
        initUI();
        bindEvents();
    }
    private void initUI() {
        account_tv.setText(SharePreferenceUtil.getStringRecord(SharePreferenceKeys.KEY_USER_NAME));
        fill_credit_info_ll.setClickable(true);
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
                TextView tmp_tv = new TextView(PersonalCenterActivity.this);
                tmp_tv.setText("招商银行: **** 2223 , 出账日: 每月3日, 最后还款日: 每月6日" );
                credit_ll.addView(tmp_tv);
                fill_credit_info_ll.setVisibility(View.GONE);
            }
        });

        cancel_credit_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //reset all msg --TODO
                fill_credit_info_ll.setVisibility(View.GONE);
            }
        });

        add_credit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TextView tmp_tv = new TextView(PersonalCenterActivity.this);
//                tmp_tv.setText("招商银行: **** 2223 , 出账日: 每月3日, 最后还款日: 每月6日" );
//                credit_ll.addView(tmp_tv);
                fill_credit_info_ll.setVisibility(View.VISIBLE);
            }
        });

    }
}

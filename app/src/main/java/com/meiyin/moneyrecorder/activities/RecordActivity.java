package com.meiyin.moneyrecorder.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.meiyin.moneyrecorder.fragments.IncomeFragment;
import com.meiyin.moneyrecorder.fragments.PayFragment;
import com.meiyin.moneyrecorder.R;

/**
 * Created by cootek332 on 18/4/1.
 */

public class RecordActivity extends FragmentActivity {

    IncomeFragment incomeFragment;
    PayFragment payFragment;

    private TextView cancel;
    private RadioGroup rgRecord;
    private RadioButton rbIn, rbOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initUI();
        bindEvents();
    }

    private void initUI() {
        incomeFragment = new IncomeFragment();
        payFragment = new PayFragment();
        cancel = (TextView) findViewById(R.id.tv_record_cancel);
        rgRecord = (RadioGroup) findViewById(R.id.rg_record);
        rbIn = (RadioButton) findViewById(R.id.rb_in);
        rbOut = (RadioButton) findViewById(R.id.rb_out);
        showFragment(payFragment);
    }

    private void bindEvents() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rgRecord.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_in:
                        showFragment(incomeFragment);
                        rbIn.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.white_FAF8F5));
                        rbOut.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.black_020202));
                        break;
                    case R.id.rb_out:
                        showFragment(payFragment);
                        rbIn.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.black_020202));
                        rbOut.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.white_FAF8F5));
                        break;
                }
            }
        });
//        findViewById(R.id.rb_out).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showFragment(payFragment);
//            }
//        });
//        findViewById(R.id.rb_in).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showFragment(incomeFragment);
//            }
//        });
    }

//    private void showPayFragment() {
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        if (payFragment.isAdded()) {
//            transaction.remove(incomeFragment);
//        }
//        if (!payFragment.isAdded()) {
//            transaction.replace(R.id.record_fragment, payFragment);
//        }
//        transaction.commit();
//    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (fragment.isAdded())
            transaction.show(fragment);
        if (!fragment.isAdded())
            transaction.replace(R.id.record_fragment, fragment);
        transaction.commit();
    }
}

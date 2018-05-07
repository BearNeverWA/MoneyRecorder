package com.meiyin.moneyrecorder.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.meiyin.moneyrecorder.Fragments.IncomeFragment;
import com.meiyin.moneyrecorder.Fragments.PayFragment;
import com.meiyin.moneyrecorder.R;

/**
 * Created by cootek332 on 18/4/1.
 */

public class RecordActivity extends FragmentActivity {
    IncomeFragment incomeFragment;
    PayFragment payFragment;
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
        showPayFragment();
    }

    private void bindEvents() {
        findViewById(R.id.pay_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPayFragment();
            }
        });
        findViewById(R.id.income_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                if (payFragment.isAdded()) {
                    transaction.remove(payFragment);
                }
                if (!incomeFragment.isAdded()) {
                    transaction.replace(R.id.record_fragment, incomeFragment);
                }
                transaction.commit();
            }
        });
    }

    private void showPayFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (payFragment.isAdded()) {
            transaction.remove(incomeFragment);
        }
        if (!payFragment.isAdded()) {
            transaction.replace(R.id.record_fragment, payFragment);
        }
        transaction.commit();
    }
}

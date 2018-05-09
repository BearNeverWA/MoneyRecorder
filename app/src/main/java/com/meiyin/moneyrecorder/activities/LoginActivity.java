package com.meiyin.moneyrecorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.meiyin.moneyrecorder.BaseActivity;
import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.utils.SharePreferenceKeys;
import com.meiyin.moneyrecorder.utils.SharePreferenceUtil;

public class LoginActivity extends BaseActivity {
    EditText user_name;
    EditText pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        bindEvents();
    }

    private void initUI() {
        SharePreferenceUtil.init(this);
        String history_user_name = SharePreferenceUtil.getStringRecord(SharePreferenceKeys.KEY_USER_NAME);
        String history_pwd = SharePreferenceUtil.getStringRecord(SharePreferenceKeys.KEY_PASSWORD);
        if (isIllegal(history_user_name, history_pwd)) {
            redirectToMain();
        }
        user_name = (EditText)findViewById(R.id.activity_main_user_name);
        pwd = (EditText)findViewById(R.id.activity_main_pwd);
    }

    private void bindEvents() {
        findViewById(R.id.activity_main_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user_name.getText().toString();
                String password = pwd.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isIllegal(username,password)) {
                    SharePreferenceUtil.setRecord(SharePreferenceKeys.KEY_USER_NAME, username);
                    SharePreferenceUtil.setRecord(SharePreferenceKeys.KEY_PASSWORD, password);
                    redirectToMain();
                } else {
                    Toast.makeText(LoginActivity.this, "账号或密码输入有误", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.activity_main_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_name.setText("");
                pwd.setText("");
            }
        });
    }

    private void redirectToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isIllegal(String username, String pwd) {
        if ("meiying".equals(username)
                && "123456".equals(pwd)) {
            return true;
        }
        return false;
    }
}

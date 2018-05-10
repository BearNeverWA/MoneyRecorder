package com.meiyin.moneyrecorder;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class BaseActivity extends AppCompatActivity {

    private TextView textLeft;
    private TextView textTitle;
    private TextView textRight;
    private RelativeLayout layout;

    public void initTitle() {
        textLeft = (TextView) findViewById(R.id.title_left);
        textTitle = (TextView) findViewById(R.id.title_center);
        textRight = (TextView) findViewById(R.id.title_right);
        layout = (RelativeLayout) findViewById(R.id.title_bar);
    }

    public void setTitle(String title) {
        initTitle();
        textTitle.setText(title);
    }

    public void setTitleLeft(String title, String left) {
        setTitle(title);
        textLeft.setText(left);
    }

    public void setTitleRight(String title, String right) {
        setTitle(title);
        textRight.setText(right);
    }

    public void setTitleAll(String title, String left, String right) {
        setTitle(title);
        textLeft.setText(left);
        textRight.setText(right);
    }

}

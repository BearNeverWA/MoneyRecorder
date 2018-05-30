package com.meiyin.moneyrecorder.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.entities.CreditItems;
import com.meiyin.moneyrecorder.http.entities.card;
import com.meiyin.moneyrecorder.utils.BmobUtils;
import com.meiyin.moneyrecorder.sqlite.SQLiteUtils;
import com.meiyin.moneyrecorder.utils.CreditUtil;
import com.meiyin.moneyrecorder.utils.SharePreferenceKeys;
import com.meiyin.moneyrecorder.utils.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by cootek332 on 18/5/15.
 */

public class PersonalCenterActivity extends Activity {
    public static Activity mActivity;


    private final String[] bank_name_spinner_string = {"中国银行", "招商银行", "农业银行"};

    private TextView account_tv;
    private TextView exit_tv;
    private LinearLayout credit_ll;
    private TextView add_credit_btn;
    private LinearLayout fill_credit_info_ll;
    private TextView confirm_credit_info_btn;
    private TextView cancel_credit_info_btn;
    private Spinner bank_name_sp;
    private EditText card_number_et;
    private TextView bill_day_tv;
    private TextView pay_day_tv;
    private LinearLayout credit_warning_ll;
    private Button credit_warning_ok;
    private Button credit_warning_cancel;

    private String msg_from_receiver;
    private ArrayList<CreditItems> creditsItems;
    private String credit_warning_msg = "";
    private int currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        mActivity = this;
        account_tv = (TextView) findViewById(R.id.personal_account);
        exit_tv = (TextView)findViewById(R.id.personal_exit);
        credit_ll = (LinearLayout)findViewById(R.id.creditcard_ll);
        add_credit_btn = (TextView)findViewById(R.id.add_credit_btn);
        fill_credit_info_ll = (LinearLayout)findViewById(R.id.fill_credit_info_ll);
        confirm_credit_info_btn = (TextView) findViewById(R.id.confirm_credit_info_btn);
        cancel_credit_info_btn = (TextView) findViewById(R.id.cancel_credit_info_btn);
        bank_name_sp = (Spinner) findViewById(R.id.bank_name);
        card_number_et = (EditText)findViewById(R.id.card_number);
        bill_day_tv = (TextView)findViewById(R.id.bill_day);
        pay_day_tv = (TextView)findViewById(R.id.pay_day);
        credit_warning_ll = (LinearLayout)findViewById(R.id.credit_warning_ll);
        credit_warning_ok = (Button)findViewById(R.id.credit_warning_ok);
        credit_warning_cancel = (Button)findViewById(R.id.credit_warning_cancel);
        getOnlineData();
        fetchAndShowCredit();
        initUI();
        bindEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAndShowCredit();
    }

    private void initUI() {
        ArrayAdapter<String> bank_name_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,bank_name_spinner_string);
        bank_name_sp.setAdapter(bank_name_adapter);

        account_tv.setText(SharePreferenceUtil.getStringRecord(SharePreferenceKeys.KEY_USER_NAME));
        fill_credit_info_ll.setClickable(true);

        //来自notification
        msg_from_receiver = getIntent().getStringExtra("msg");
        if (!TextUtils.isEmpty(msg_from_receiver)) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
            currentDate = Integer.parseInt(dateFormat.format(calendar.getTime()));

            CreditItems item = CreditUtil.getWarningCreditItems(currentDate);

            String bank_name = item.getBankName();
            String number = item.getCardNumber();
            credit_warning_msg = "今天是您尾号为" + number + "的【" + bank_name + "】信用卡的还款日";
            ((TextView)findViewById(R.id.credit_warning_msg)).setText(credit_warning_msg);
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
                String bank = bank_name_sp.getSelectedItem().toString();
                String card_number = card_number_et.getText().toString();
                String bill_day = bill_day_tv.getText().toString();
                String pay_day = pay_day_tv.getText().toString();
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
                if (card_number.length() != 4) {
                    Toast.makeText(PersonalCenterActivity.this, "请输入卡号后四位", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Integer.parseInt(card_number);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PersonalCenterActivity.this, "请在卡号栏中输入号码", Toast.LENGTH_SHORT).show();
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
                uploadCredit(bank, card_number, Integer.parseInt(bill_day), Integer.parseInt(pay_day));
                fetchAndShowCredit();
                clearTable();
            }
        });

        bill_day_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar ca = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(PersonalCenterActivity.this, onBillDayDateSetListener, ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DATE));
                datePickerDialog.show();
            }
        });

        pay_day_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar ca = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(PersonalCenterActivity.this, onPayDayDateSetListener, ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DATE));
                datePickerDialog.show();
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
//                String record_days = SharePreferenceUtil.getStringRecord(SharePreferenceKeys.KEY_CREDIT_WARNING_DATES);
                if (creditsItems.size() >= 2) {
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
                int count = CreditUtil.getWarningCount(currentDate);
                if (count == 0) {
                    SharePreferenceUtil.setRecord(SharePreferenceKeys.KEY_WARNED_THIS_MONTH_CARD_1, true);
                } else if (count == 1) {
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

    private void recordCredit(String bankName, String cardNumber, int billDay, int payDay) {
        CreditItems item = new CreditItems(null, null, bankName, cardNumber, billDay, payDay, 0, 0);
        SQLiteUtils.insertCredit(item);
    }

    private void uploadCredit(String bankName, final String cardNumber, int billDay, int payDay) {
        String userName = SharePreferenceUtil.getStringRecord(SharePreferenceKeys.KEY_USER_NAME);
        String familyName = SharePreferenceUtil.getStringRecord(SharePreferenceKeys.KEY_FAMILY_NAME);
        SaveListener<String> callback = new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                SQLiteUtils.uploadedCredit(objectId, cardNumber);
            }
        };
        BmobUtils.uploadCredits(userName, familyName, cardNumber, bankName, billDay, payDay, callback);
    }

    private void getOnlineData() {
        if (SQLiteUtils.getAllCredits().size() > 0) {
            return;
        }
        String userName = SharePreferenceUtil.getStringRecord(SharePreferenceKeys.KEY_USER_NAME);
        QueryListener<JSONArray> callback = new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    //仅支持显示两张
                    if (i >= 2) {
                        break;
                    }
                    try {
                        JSONObject obj = (JSONObject)jsonArray.get(i);
                        String objectId = obj.getString("objectId");
                        String bankName = obj.getString(card.FILED_BANK_NAME);
                        String cardNumber = obj.getString(card.FILED_CARD_NUMBER);
                        int billDay = obj.getInt(card.FILED_BILL_DAY);
                        int payDay = obj.getInt(card.FILED_PAY_DAY);
                        CreditItems creditItem = new CreditItems(objectId, null, bankName, cardNumber, billDay, payDay, 0, 1);
                        SQLiteUtils.insertCredit(creditItem);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                onResume();
            }
        };
        BmobUtils.getCredits(userName, callback);
    }

    private void fetchAndShowCredit() {
        credit_ll.removeAllViews();
        creditsItems = SQLiteUtils.getCredits();
        for (int i = 0; i < creditsItems.size(); i++) {
            CreditItems item = creditsItems.get(i);
            final TextView tmp_tv = new TextView(PersonalCenterActivity.this);
            tmp_tv.setHeight(100);
            tmp_tv.setGravity(Gravity.CENTER_VERTICAL);
            tmp_tv.setText(item.getBankName() + ": " + item.getCardNumber() + ", 出账日期: " + item.getBillDay() + ", 还款日期: " + item.getPayDay());
            tmp_tv.setTextSize(16);
            final int finalI = i;
            tmp_tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PersonalCenterActivity.this);
                    builder.setTitle("确认删除")
                            .setMessage("是否确认删除该信用卡信息?")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String objectId = creditsItems.get(finalI).getObjectId();
                                    String id = creditsItems.get(finalI).getId();
                                    if (!TextUtils.isEmpty(objectId)) {
                                        BmobUtils.deleteCredits(objectId, id);
                                    } else {
                                        SQLiteUtils.deleteCredit(id);
                                    }
                                    credit_ll.removeView(tmp_tv);
                                    creditsItems.remove(finalI);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create().show();
                    return false;
                }
            });
            credit_ll.addView(tmp_tv);
            findViewById(R.id.content_seperate_line).setVisibility(View.VISIBLE);
        }
    }

    private DatePickerDialog.OnDateSetListener onBillDayDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            bill_day_tv.setText(dayOfMonth + "");
        }
    };

    private DatePickerDialog.OnDateSetListener onPayDayDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            pay_day_tv.setText(dayOfMonth + "");
        }
    };

    private void clearTable() {
        bank_name_sp.setSelection(0);
        card_number_et.setText("");
        bill_day_tv.setText("");
        pay_day_tv.setText("");
        fill_credit_info_ll.setVisibility(View.GONE);
    }
}

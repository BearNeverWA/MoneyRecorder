package com.meiyin.moneyrecorder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.utils.DateUtil;

import java.util.List;
import java.util.Map;

public class ListAdapter extends ArrayAdapter<Map<String, Object>> {

    private Context context;

    public ListAdapter(@NonNull Context context, int resource, @NonNull List<Map<String, Object>> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Map map = getItem(position);
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_main, parent, false);
            holder = new ViewHolder();
            holder.tvDay = (TextView) view.findViewById(R.id.tv_day);
            holder.tvDayOfWeek = (TextView) view.findViewById(R.id.tv_day_of_week);
            holder.tvType = (TextView) view.findViewById(R.id.tv_type);
            holder.tvInOrOut = (TextView) view.findViewById(R.id.tv_in_or_out);
            holder.tvDetail = (TextView) view.findViewById(R.id.tv_detail);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        String time = (String) map.get("setTime");
        String money = (String) map.get("money");

        holder.tvDay.setText(time.split("月")[1].split("日")[0]);
        holder.tvDayOfWeek.setText(DateUtil.getDayForWeek((String) map.get("completeTime")));
        holder.tvType.setText((String) map.get("buyClassifyOne"));
        if (money.contains("+")) {
            holder.tvInOrOut.setText("[收入]");
            holder.tvDetail.setTextColor(ContextCompat.getColor(context, R.color.green_008b00));
        } else if (money.contains("-")) {
            holder.tvInOrOut.setText("[支出]");
            holder.tvDetail.setTextColor(ContextCompat.getColor(context, R.color.red_ff6347));
        }
        holder.tvDetail.setText("￥" + money.substring(1, money.length()));
        return view;
    }

    class ViewHolder {
        TextView tvDay;
        TextView tvDayOfWeek;
        TextView tvType;
        TextView tvInOrOut;
        TextView tvDetail;
    }
}

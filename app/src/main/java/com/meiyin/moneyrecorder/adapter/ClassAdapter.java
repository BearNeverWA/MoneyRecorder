package com.meiyin.moneyrecorder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meiyin.moneyrecorder.R;
import com.meiyin.moneyrecorder.entities.RvItem;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private List<RvItem> rvItemList;

    public ClassAdapter(List<RvItem> rvItems) {
        this.rvItemList = rvItems;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView classImg;
        TextView className;

        public ViewHolder(View view) {
            super(view);
            classImg = (ImageView) view.findViewById(R.id.iv_class);
            className = (TextView) view.findViewById(R.id.tv_class);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        RvItem rvItem = rvItemList.get(position);
        holder.classImg.setImageResource(rvItem.getResId());
        holder.className.setText(rvItem.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rvItemList.size();
    }

}

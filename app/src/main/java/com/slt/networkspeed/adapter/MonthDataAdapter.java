package com.slt.networkspeed.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.slt.networkspeed.R;
import com.slt.networkspeed.bean.DataInfo;

import java.util.List;



public class MonthDataAdapter extends Adapter<MonthDataAdapter.MonthDataViewHolder> {
    public static final String TAG = "MonthDataAdapter";

    public List<DataInfo> dataInfoList;
    private Context mContext;

    public MonthDataAdapter(List<DataInfo> dataList, Context context){
        dataInfoList = dataList;
        this.mContext = context;
    }

    @Override
    public MonthDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_card_layout, parent, false);
        Log.i(TAG, "----onCreateViewHolder----");
        return new MonthDataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MonthDataViewHolder holder, int position) {
        Log.i(TAG, "----onBindViewHolder----");
        DataInfo di = dataInfoList.get(position);

        //只显示月河日，并且月和日之间有回车换行
        String[] sourceArray = di.date.split(" ");
        if(sourceArray !=null && sourceArray.length > 2) {
            String date = String.format("%s%n%s", sourceArray[0], sourceArray[1]);
            holder.tvDate.setText(date);
        }else{
            holder.tvDate.setText(di.date);
        }
        holder.tvMobile.setText(di.mobile);
        holder.tvWifi.setText(di.wifi);
        holder.tvTotal.setText(di.total);

        holder.tvMobileUnit.setText(di.mobileUnit);
        holder.tvMobileUnit.setText(di.wifiUnit);
        holder.tvMobileUnit.setText(di.totalUnit);

        if(position == (dataInfoList.size() - 1)){
            holder.tvDate.setTextColor(mContext.getResources().getColor(R.color.total_title_text_color));
        }
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "----getItemCount----dataInfoList.size() ="+dataInfoList.size());
        return dataInfoList.size();
    }


    public static class MonthDataViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvDate;

        protected TextView tvWifi;
        protected TextView tvMobile;
        protected TextView tvTotal;

        protected TextView tvWifiUnit;
        protected TextView tvMobileUnit;
        protected TextView tvTotalUnit;

        public MonthDataViewHolder(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.id_date);
            tvMobile = (TextView) itemView.findViewById(R.id.id_mobile);
            tvWifi = (TextView) itemView.findViewById(R.id.id_wifi);
            tvTotal = (TextView) itemView.findViewById(R.id.id_total);

            tvWifiUnit = (TextView) itemView.findViewById(R.id.id_mobile_unit);
            tvMobileUnit = (TextView) itemView.findViewById(R.id.id_wifi_unit);
            tvTotalUnit = (TextView) itemView.findViewById(R.id.id_total_unit);
        }
    }
}

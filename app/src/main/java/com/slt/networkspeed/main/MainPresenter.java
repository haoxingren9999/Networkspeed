package com.slt.networkspeed.main;

import android.content.Context;
import android.util.Log;

import com.slt.networkspeed.bean.DataInfo;
import com.slt.networkspeed.mvp.BasePresenterImpl;

import java.util.List;


public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {
    //持有一个model对象,以及一个view对象--MainActivity，是model和view之间的桥梁，model和view不直接通信
    public static final String TAG = "MainPresenter";

    public MainModel mMainModel;

    public Context mContext;

    void initModel() {
        mContext = mView.getContext();
        mMainModel = new MainModel(this);
        Log.i(TAG, "----initModel----");
    }


    @Override
    public List<DataInfo> createList(int size) {
        return mMainModel.createList(size);
    }

    @Override
    public void setTodayView() {
        Log.i(TAG, "----setTodayView----");
        //List<String> total = dataFormate(total_wifi, total_mobile, total_wifi + total_mobile);
        DataInfo dataInfo = mMainModel.todayData();

        //只显示月河日，并且月和日之间有回车换行
        String[] sourceArray = dataInfo.date.split(" ");
        if(sourceArray !=null && sourceArray.length > 2) {
            String date = String.format("%s%n%s", sourceArray[0], sourceArray[1]);
            ((MainActivity)mView).tvTodayDate.setText(date);
        }else{
            ((MainActivity)mView).tvTodayDate.setText(dataInfo.date);
        }

        ((MainActivity)mView).tvTodayMobile.setText(dataInfo.mobile);
        ((MainActivity)mView).tvTodayWifi.setText(dataInfo.wifi);
        ((MainActivity)mView).tvTodayTotal.setText(dataInfo.total);

        ((MainActivity)mView).tvTodayMobileUnit.setText(dataInfo.mobileUnit);
        ((MainActivity)mView).tvTodayWifiUnit.setText(dataInfo.wifiUnit);
        ((MainActivity)mView).tvTodayTotalUnit.setText(dataInfo.totalUnit);
    }

    @Override
    public void clearExtraData() {
        mMainModel.clearExtraData();
    }

    @Override
    public void refreshTotalList(List<DataInfo> monthData) {
        Log.e(TAG, "refreshTotalList monthData ="+monthData.toString());
        ((MainActivity)mView).dataAdapter.dataInfoList = monthData;
        ((MainActivity)mView).dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshListItem(int position, DataInfo dataInfo) {
        Log.e(TAG, "refreshListItem position= "+position);
        ((MainActivity)mView).dataAdapter.dataInfoList.set(position,dataInfo);
        ((MainActivity)mView).dataAdapter.notifyItemChanged(position);
    }

    @Override
    public void loopRefreshList() {
        Log.i(TAG, "----loopRefreshList----");
        mMainModel.loopRefreshList();
    }

    @Override
    public void modifyThreadName(String newName) {
        Log.i(TAG, "----modifyThreadName----newName="+newName);
        mMainModel.dataUpdate.setName(newName);
    }

    @Override
    public void restartThread() {
        if(mMainModel.dataUpdate !=null && (!mMainModel.dataUpdate.isAlive())){
            mMainModel.loopRefreshList();
        }
    }
}

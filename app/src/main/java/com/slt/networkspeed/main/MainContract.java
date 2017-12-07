package com.slt.networkspeed.main;

import com.slt.networkspeed.bean.DataInfo;
import com.slt.networkspeed.mvp.BasePresenter;
import com.slt.networkspeed.mvp.BaseView;

import java.util.List;


public class MainContract {
    interface View extends BaseView {
        //设置今日流量
        public void setTodayViewData(List<String> list);
    }

    interface Presenter extends BasePresenter<View> {
        //创建30日的流量列表
        public List<DataInfo> createList(int size);
        //设置今日流量
        public void setTodayView();

        //清除30日外的额外的数据
        public void clearExtraData();

        //刷新整个列表
        public void refreshTotalList(List<DataInfo> monthData);

        //刷新列表某个列表项
        public void refreshListItem(int position, DataInfo dataInfo);

        //无限循环刷新列表
        public void loopRefreshList();

        //更改线程名称
        public void modifyThreadName(String newName);

        //重新启动刷新线程
        public void restartThread();
    }

    interface Model{
        //创建30日的流量列表
        public List<DataInfo> createList(int size);

        //清除30日外的额外的数据
        public void clearExtraData();

        //无限循环刷新列表
        public void loopRefreshList();
    }
}

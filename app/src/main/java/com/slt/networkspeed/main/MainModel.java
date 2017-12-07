package com.slt.networkspeed.main;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;


import com.slt.networkspeed.R;
import com.slt.networkspeed.bean.DataInfo;
import com.slt.networkspeed.utils.Constants;
import com.slt.networkspeed.utils.WorkSpeedUtils;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;




public class MainModel implements MainContract.Model {
    public static final String TAG = "MainModel";

    //持有一个prsenter对象
    private MainContract.Presenter mPresenter;

    private final DecimalFormat df = new DecimalFormat("#.##");

    final static String MEGABYTE = "MB", GIGABYTE = "GB";

    private double total_wifi = 0;
    private double total_mobile = 0;

    private double today_wifi = 0;
    private double today_mobile = 0;

    private Handler vHandler = new Handler();

    public Thread dataUpdate = null;

    private String today_date = null;

    public MainModel(MainContract.Presenter presenter){
        mPresenter = presenter;
    }

    /**
     * 创建30日数据流量列表
     * @param size
     * @return
     */
    @Override
    public List<DataInfo> createList(int size) {
        Log.i(TAG, "----createList----");
        List<DataInfo> result = new ArrayList<>();
        total_wifi = 0;
        total_mobile = 0;

        double wTemp, mTemp, tTemp;

        String wifi = "0", mobile = "0", total = "0";
        SharedPreferences sp_month = ((MainPresenter)mPresenter).mContext.getSharedPreferences(Constants.MONTH_DATA, Context.MODE_PRIVATE);

        for (int i = 1; i <= size; i++) {
            Log.i(TAG, "----createList---- i="+i);
            if (i == 1) {
                //result.add(todayData());
                //今日数据不需要放到列表中显示
                todayData();
                continue;
            }
            Calendar calendar = Calendar.getInstance();

            calendar.add(Calendar.DATE, (1 - i)); // day decrease to get previous day

            String mDate = Constants.SDF.format(calendar.getTime());// get  date
            List<String> allData = new ArrayList<>();
            Log.i(TAG, "----createList---- mDate"+mDate);

            //check date availabe or not
            if (sp_month.contains(mDate)) {
                Log.i(TAG, "----createList---- sp_month.contains is true");
                String sData = sp_month.getString(mDate, null); // get saved data
                Log.i(TAG, "----createList---- sData"+sData);
                try {
                    JSONObject jOb = new JSONObject(sData);
                    wifi = jOb.getString("WIFI_DATA");
                    mobile = jOb.getString("MOBILE_DATA");

                    // total = jOb.getString("TOTAL_DATA");
                    wTemp = (Long.parseLong(wifi) / 1048576.0);
                    mTemp = (Long.parseLong(mobile) / 1048576.0);
                    //tTemp = (double) Long.parseLong(total) / 1000000.0;

                    tTemp = wTemp + mTemp;
                    allData = dataFormate(wTemp, mTemp, tTemp);
                    //Log.e(TAG, Integer.toString(i) + " " + wifi + mobile + Double.toString(wTemp));
                    //count for total
                    total_wifi += wTemp;
                    total_mobile += mTemp;
                    Log.i(TAG, "----createList---- total_wifi"+total_wifi+",total_mobile="+total_mobile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.i(TAG, "----createList---- sp_month.contains is false");
                allData = dataFormate(0, 0, 0);
                Log.i(TAG, "----createList---- allData="+allData.toString());
            }

            DataInfo dataInfo = new DataInfo();
            dataInfo.date = mDate;
            dataInfo.wifi = allData.get(0);
            dataInfo.wifiUnit = allData.get(0);
            dataInfo.mobile = allData.get(2);
            dataInfo.mobileUnit = allData.get(3);
            dataInfo.total = allData.get(4);
            dataInfo.totalUnit = allData.get(5);

            result.add(dataInfo);
        }

        //全部数据按需求需要放到列表中显示
        DataInfo totalInfo = totalData();
        Log.i(TAG, "----createList---- totalData totalInfo="+totalInfo.toString());
        result.add(totalInfo);

        return result;
    }

    @Override
    public void clearExtraData() {
        Log.i(TAG, "----clearExtraData----");
        SharedPreferences sp_month = ((MainPresenter)mPresenter).mContext.getSharedPreferences(Constants.MONTH_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp_month.edit();

        //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

        for (int i = 40; i <= 1000; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, (1 - i));
            String mDate = Constants.SDF.format(calendar.getTime());// get  date

            if (sp_month.contains(mDate)) {
                editor.remove(mDate);
            }
        }
        editor.apply();
    }

    @Override
    public void loopRefreshList() {
        Log.i(TAG, "----MaiModel loopRefreshList----");
        dataUpdate = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "----loopRefreshList---- dataUpdate.getName() = "+dataUpdate.getName());
                while (!dataUpdate.getName().equals("stopped")) {

                    Calendar ca = Calendar.getInstance();
                    final String temp_today = Constants.SDF.format(ca.getTime());// get today's date
                    Log.i(TAG, "----loopRefreshList---- temp_today = "+temp_today);
                    Log.i(TAG, "----loopRefreshList---- today_date = "+today_date);
                    vHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // check today's date
                            if (temp_today.equals(today_date)) {
                                Log.e(TAG, "loopRefreshList mPresenter.refreshListItem ");
                                //mPresenter.refreshListItem(0, todayData());
                                mPresenter.setTodayView();
                                mPresenter.refreshListItem(29, totalData());

                                //Log.e(TAG, "temp_today = "+temp_today);
                            } else {
                                today_wifi = 0;
                                today_mobile = 0;

                                List<DataInfo> monthData = createList(30);  // to update total month data
                                mPresenter.refreshTotalList(monthData);
                                Log.e(TAG, "loopRefreshList mPresenter.refreshTotalList ");
                                mPresenter.setTodayView();
                                //dataAdapter.dataList = monthData;  //update adapter list
                                //dataAdapter.notifyDataSetChanged();

                                //monthData.set(0, todayData());
                                //dataAdapter.notifyItemChanged(0);

                                //Log.e(TAG,temp_today);
                            }

                            //totalData(); //call main thread
                            // Log.e(TAG, toString().valueOf(total_wifi));
                        }
                    });

                    try {
                        Thread.sleep(WorkSpeedUtils.getFreshTime(((MainPresenter)mPresenter).mContext.getApplicationContext()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //  progressStatus--;
                }
            }
        });

        dataUpdate.setName("started");

        dataUpdate.start();
    }

    /**
     * 格式化数据
     * @param wifi
     * @param mobile
     * @param total
     * @return
     */
    public List<String> dataFormate(double wifi, double mobile, double total) {

        List<String> allData = new ArrayList<>();
        //DecimalFormat df = new DecimalFormat("#.##");
        //check less than Gigabyte or not
        if (wifi < 1024) {
            allData.add(df.format(wifi)); // consider 2 value after decimal point
            allData.add(MEGABYTE);
        } else {
            allData.add(df.format(wifi / 1024));
            allData.add(GIGABYTE);
        }

        if (mobile < 1024) {
            allData.add(df.format(mobile)); // consider 2 value after decimal point
            allData.add(MEGABYTE);
        } else {
            allData.add(df.format(mobile / 1024));
            allData.add(GIGABYTE);
        }

        if (total < 1024) {
            allData.add(df.format(total)); // consider 2 value after decimal point
            allData.add(MEGABYTE);
        } else {
            allData.add(df.format(total / 1024));
            allData.add(GIGABYTE);
        }
        return allData;
    }

    /**
     * 获取今日流量数据
     * @return
     */
    public DataInfo todayData() {
        Log.i(TAG, "----todayData---- ");
        //List<DataInfo> listToday = new ArrayList<>();

        Calendar ca = Calendar.getInstance();
        today_date = Constants.SDF.format(ca.getTime());// get today's date

        double wTemp = 0, mTemp = 0, tTemp = 0;
        try {
            SharedPreferences sp = ((MainPresenter)mPresenter).mContext.getSharedPreferences(Constants.TODAY_DATA, Context.MODE_PRIVATE);
            // convert to megabyte
            wTemp = sp.getLong("WIFI_DATA", 0) / 1048576.0;
            mTemp = sp.getLong("MOBILE_DATA", 0) / 1048576.0;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        tTemp = wTemp + mTemp;

        List<String> allData = dataFormate(wTemp, mTemp, tTemp);

        //今天的数据发生变化后，wifi和mobile的总量需要更新
        total_wifi = total_wifi + (wTemp - today_wifi);
        total_mobile = total_mobile + (mTemp - today_mobile);
        //更新今日数据
        today_wifi = wTemp;
        today_mobile = mTemp;

        DataInfo dataInfo = new DataInfo();

        dataInfo.date = today_date;
        dataInfo.wifi = allData.get(0);
        dataInfo.wifiUnit = allData.get(1);
        dataInfo.mobile = allData.get(2);
        dataInfo.mobileUnit = allData.get(3);
        dataInfo.total = allData.get(4);
        dataInfo.totalUnit = allData.get(5);

        //listToday.add(dataInfo);

        Log.i(TAG, "----todayData----dataInfo.wifi=" + dataInfo.wifi +",dataInfo.mobile="+ dataInfo.mobile + ",dataInfo.total="+dataInfo.total);

        return dataInfo;
    }

    /**
     * 获取所有天的流量
     */
    public DataInfo totalData() {
        DataInfo dataInfo = new DataInfo();

        List<String> total = dataFormate(total_wifi, total_mobile, total_wifi + total_mobile);

        dataInfo.date = ((MainPresenter)mPresenter).mContext.getString(R.string.str_total);
        dataInfo.wifi = total.get(0);
        dataInfo.wifiUnit = total.get(1);
        dataInfo.mobile = total.get(2);
        dataInfo.mobileUnit = total.get(3);
        dataInfo.total = total.get(4);
        dataInfo.totalUnit = total.get(5);

        return dataInfo;
    }
}

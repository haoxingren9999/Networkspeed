package com.slt.networkspeed.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.slt.networkspeed.MyApp;

import com.slt.networkspeed.R;
import com.slt.networkspeed.bean.Package;
import com.slt.networkspeed.main.MainActivity;
import com.slt.networkspeed.utils.Constants;
import com.slt.networkspeed.utils.NetworkUtil;
import com.slt.networkspeed.utils.RetrieveData;
import com.slt.networkspeed.utils.SharedPrefsUtil;
import com.slt.networkspeed.utils.WorkSpeedUtils;

import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;




public class SpeedService extends Service {
    public static final String TAG = "SpeedService";
    PendingIntent contentIntent;
    public static boolean service_status = false;
    public static ArrayList<Float> listData=new ArrayList<>();
    NotificationManager mNotificationManager;
    Notification mNotification;
    NotificationCompat.Builder builder;
    Thread dataThread;
    private int level=0;
    private String up= "0";
    private String down ="0";
    private String B="B/s";
    private String b="b/s";
    private String KB="KB/s";
    private String kb="Kb/s";
    private String MB="MB/s";
    private String mb="Mb/s";
    private String upUnit= "KB/s";
    private String downUnit ="KB/s";
    private final DecimalFormat df = new DecimalFormat("#.##");
    private final DecimalFormat dfNotification = new DecimalFormat("#.0");
    final static String MEGABYTE = "MB", GIGABYTE = "GB";
    private String wifiDate="0MB";
    private String mobileDate ="MB";
//    private long timePre=0;//系统上一个20秒的时间
    private boolean isFirst = true;//出事进去记录一下时间

    private Method setForeground = null;
    private Method startForeground = null;
    private Method stopForeground = null;

    private final Class<?>[] setForgroundSignature = {Boolean.TYPE};
    private final Class<?>[] startForgroundSignature = {Integer.TYPE, Notification.class};
    private final Class<?>[] stopForgroundSignature ={Boolean.TYPE};

    public static  final int NOTIFICATION_ID_FORGROUND_SERVICE = 10010;

    /**
     * 刷新通知栏的runnable
     */
    final class MyThreadClass implements Runnable {

        int service_id;

        MyThreadClass(int service_id) {
            this.service_id = service_id;
        }

        @Override
        public void run() {
            //int i = 0;
            synchronized (this) {
                while (dataThread.getName() == "showNotification") {
                    //Log.e(TAG, Integer.toString(service_id) + " " + Integer.toString(i));
                    getData();
                    try {
//                        Log.e("123"," Fresh time "+WorkSpeedUtils.getFreshTime(getApplicationContext()));
                        wait(WorkSpeedUtils.getFreshTime(getApplicationContext()));
                        // i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //stopSelf(service_id);
            }
        }
    }

    private String  getLiuliang(long date ) {
        double temp = (date / 1048576.0);
        if (temp < 1024) {
            df.format(temp); // consider 2 value after decimal point
            return  df.format(temp)+MEGABYTE;
        } else {
            return  df.format(temp)+GIGABYTE;
        }

    }
    public void showNotification(long download,long upload) {
        //读取速度单位
        String wifiName = NetworkUtil.getWifiName(getApplicationContext());
        boolean isKB = SharedPrefsUtil.getValue(getApplicationContext(), SharedPrefsUtil.SETTING,SharedPrefsUtil.SPEED_UNIT,true);
        getNotificationInfo(download,upload,isKB);
//        long mobile= SharedPrefsUtil.getValue(getApplicationContext(),TODAY_DATA,"MOBILE_DATA",0);
//        long wifi= SharedPrefsUtil.getValue(getApplicationContext(),TODAY_DATA,"WIFI_DATA",0);
        SharedPreferences sp_day = getSharedPreferences(Constants.TODAY_DATA, Context.MODE_PRIVATE);
        long mobile = sp_day.getLong("MOBILE_DATA", 0);
        long wifi = sp_day.getLong("WIFI_DATA", 0);
        mobileDate=getLiuliang(mobile);
        wifiDate=getLiuliang(wifi);

        if(isKB){
            builder.setSmallIcon(R.drawable.ic_kb_s,level);
        }else{

            builder.setSmallIcon(R.drawable.ic_kbps,level);
        }

//        Log.e("123", "down === " + down + " unit " + downUnit);
        builder.setContentTitle(getString(R.string.download) + down + downUnit + "  " + getString(R.string.update) + (up) + upUnit + "  " + wifiName);
        builder.setContentText(getString(R.string.mobile) + mobileDate + "  " + getString(R.string.wifi) + wifiDate);
//        builder.setSubText();
        builder.setContentIntent(contentIntent);
        builder.setWhen(0)
                .setPriority(Notification.PRIORITY_HIGH)
                .setOngoing(true);
        mNotification = builder.build();
        boolean notificationFlag= SharedPrefsUtil.getValue(getApplicationContext(),SharedPrefsUtil.SETTING,SharedPrefsUtil.NOTIFICATION_SWITCH,true);

        if (notificationFlag&&autoHide()) {
            //mNotificationManager.notify(0, mNotification);

            //设置为前台进程
            startForegroundCompat(NOTIFICATION_ID_FORGROUND_SERVICE, mNotification);
        } else {
            stopForegroundCompat(NOTIFICATION_ID_FORGROUND_SERVICE);
            mNotificationManager.cancel(NOTIFICATION_ID_FORGROUND_SERVICE);
        }

    }

    /**
     * 20秒没有走下载流量
     * @return
     */
    private boolean autoHide() {
        boolean autoSwitchFlag = SharedPrefsUtil.getValue(getApplicationContext(), SharedPrefsUtil.SETTING, SharedPrefsUtil.AUTOHIDE, false);
        if (autoSwitchFlag) {
            long time = System.currentTimeMillis();
            long timePre =SharedPrefsUtil.getValue(getApplicationContext(),SharedPrefsUtil.SETTING,SharedPrefsUtil.IS_NOT_0,System.currentTimeMillis());
            if (time - timePre < 20000) {
                //显示通知
//                Log.e("123","小于 "+(time-timePre));
                return true;
            }else{
//                Log.e("123","大于"+(time-timePre));
                return false;

            }
        } else {
            return true;
        }

    }

    private void getNotificationInfo(long downLong, long upLong, boolean isKB) {
        int downInt = 0;
        int upInt = 0;
        float time =WorkSpeedUtils.getFreshTime(getApplicationContext())/1000;
        if (isKB) {

            downInt = (int) (downLong / 1024/time);
            upInt = (int) (upLong / 1024/time);
//            Log.e("123", "downInt " + downInt);
//            Log.e("123", "upInt " + upInt);
            if (upInt == 0) {
               upInt = (int)(upLong/time);
                up = String.valueOf(upInt);
                upUnit = B;
            } else if (upInt < 1000 && upInt > 0) {
                up = String.valueOf(upInt);
                upUnit = KB;
//                Log.e("123", "up<1000 " + up);
            } else if (upInt >= 1000 && upInt < 9950) {
                upUnit=MB;
                String str = dfNotification.format(upInt / 1024f);
//                Log.e("123", "leverStrDown " + str);
                up = str;
            } else if (level >= 9950) {
                upUnit=MB;
                up = "10+";
            }
//下载
            if (downInt == 0) {
                downInt = (int)(downLong/time);
                down = String.valueOf(downInt);
                downUnit = B;
                level = 1;
            } else if (downInt < 999 && downInt > 0) {
                downUnit=KB;
                    level = downInt+1;
                down = String.valueOf(downInt);
//                Log.e("123", "down<1000 " + down);
            }
            else if (downInt >= 999 && downInt < 9950) {
                if(downInt==999){
                    level=1001;
                }
                downUnit=MB;
//                DecimalFormat df = new DecimalFormat("#.0");
                String strValue = dfNotification.format(downInt / 1024f);
//                Log.e("123", "leverStrUp " + strValue);
                String str = strValue.replace(".", "");
                int z = Integer.parseInt(str) * 100;
//                Log.e("123", "zUp = " + z);
                level = z;
                down = strValue;
            } else if (level >= 9950) {
                downUnit=MB;
                level = 10000;
                down = "10+";
            }



        } else {

            downInt = (int) (downLong * 8/ 1024/time) ;
            upInt = (int) (upLong * 8/ 1024/time) ;
            if (upInt == 0) {
                upInt = (int)(upLong*8/time);
//                Log.e("123", "upInt====" + upInt);
                up = String.valueOf(upInt);
                upUnit = b;
            } else if (upInt < 1000 && upInt > 0) {
                upUnit = kb;
                up = String.valueOf(upInt);
//                Log.e("123", "up<1000 " + up);

            } else if (upInt >= 1000 && upInt < 9950) {
                upUnit=mb;
                String str = dfNotification.format(upInt / 1024f);
//                Log.e("123", "leverStrDown " + str);
                up = str;
            } else if (level >= 9950) {
                upUnit=mb;
                up = "10+";
            }
//下载
            if (downInt == 0) {
                downInt = (int)(downLong*8/time);
//                Log.e("123", "downInt====" + downInt);
                down = String.valueOf(downInt);
                downUnit = b;
                level = 1;
            } else if (downInt < 999 && downInt > 0) {
                downUnit=kb;
                level = downInt+1;
                down = String.valueOf(downInt);
//                Log.e("123", "down<1000 " + down);
            } else if (downInt >= 999 && downInt < 9950) {
                if(downInt==999){
                    level=1001;
                }
                downUnit=mb;
                String strValue = dfNotification.format(downInt / 1024f);
//                Log.e("123", "leverStrUp " + strValue);
                String  str = strValue.replace(".", "");
                int z = Integer.parseInt(str) * 100;
//                Log.e("123", "zUp = " + z);
                level = z;
                down = strValue;
            } else if (level >= 9950) {
                downUnit=mb;
                level = 10000;
                down = "10+";
            }
        }



    }

    @Override
    public void onCreate() {
        super.onCreate();
        initForeground();
        initNotifacation();
    }
    private void initForeground(){
        Class<?> localClass = this.getClass();

        try {
            setForeground = localClass.getMethod("setForeground", this.setForgroundSignature);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            startForeground = localClass.getMethod("startForeground", this.startForgroundSignature);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            stopForeground = localClass.getMethod("stopForeground", this.stopForgroundSignature);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void  initNotifacation() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.setAction("com.shere.easynetworkspeed.start.main");
        builder = new NotificationCompat.Builder(this);
        contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.mipmap.logo);
//        builder.setContentTitle(getString(R.string.app_name));
        builder.setLargeIcon(bd.getBitmap());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sp_day = getSharedPreferences(Constants.TODAY_DATA, Context.MODE_PRIVATE);
        Log.i("SpeedService","--------------------onStartCommand");
        //check today_date empty or not
        //if not then create pref key by date
        if (!sp_day.contains("today_date")) {
            SharedPreferences.Editor editor_day = sp_day.edit();
            Calendar ca = Calendar.getInstance();
            //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            String tDate = Constants.SDF.format(ca.getTime());// get today's date

            editor_day.putString("today_date", tDate);
            editor_day.apply();
        }

        if (!service_status) {
            service_status = true;
            dataThread = new Thread(new MyThreadClass(startId));
            dataThread.setName("showNotification");
            dataThread.start();
            //Log.i("SpeedService","--------------------onStartCommand　FlowChartActivity.liveFlag ="+FlowChartActivity.liveFlag);
            for(int i=0;i<61;i++)
            {
                listData.add(0f); //初始化数据
            }
            //if(FlowChartActivity.liveFlag){
            final ArrayList<Package> list = MyApp.list;

            //}
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service_status = false;
        stopForegroundCompat(NOTIFICATION_ID_FORGROUND_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void getData() {
        long mobileData, totalData, wifiData, saved_mobileData, saved_wifiData, saved_totalData, receiveData;

        String saved_date, tDate;
        List<Long> allData;

        String network_status = NetworkUtil.getConnectivityStatusString(getApplicationContext());

        allData = RetrieveData.findData();

        Long mDownload, mUpload;

        mDownload = allData.get(0);
        mUpload = allData.get(1);
        if(isFirst){
            SharedPrefsUtil.putValue(getApplicationContext(),SharedPrefsUtil.SETTING,SharedPrefsUtil.IS_NOT_0,System.currentTimeMillis());
            isFirst=false;
        }
          if(mDownload>0){
              SharedPrefsUtil.putValue(getApplicationContext(),SharedPrefsUtil.SETTING,SharedPrefsUtil.IS_NOT_0,System.currentTimeMillis());
          }

        boolean notificationFlag= SharedPrefsUtil.getValue(getApplicationContext(),SharedPrefsUtil.SETTING,SharedPrefsUtil.NOTIFICATION_SWITCH,true);
//        Log.e("123", "mDownload " + mDownload);
//        Log.e("123", "mUpload " + mUpload);

        if (notificationFlag&&autoHide()) {
            showNotification( mDownload, mUpload);
        } else {
            stopForegroundCompat(NOTIFICATION_ID_FORGROUND_SERVICE);
            mNotificationManager.cancel(NOTIFICATION_ID_FORGROUND_SERVICE);
        }

        receiveData = mDownload + mUpload;

        /*if (notification_status) {
            showNotification(receiveData);
        }*/

        wifiData = 0;
        mobileData = 0;
        totalData = 0;

        if (network_status.equals("wifi_enabled")) {
            totalData = receiveData;
            wifiData = receiveData;
        } else if (network_status.equals("mobile_enabled")) {
            totalData = receiveData;
            mobileData = receiveData;
        }

        // Log.e(TAG, Long.toString(totalData) + " " + Long.toString(wifiData) + " " + Long.toString(mobileData));
        // mobileData = mobileData;

        Calendar ca = Calendar.getInstance();
        //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        tDate = Constants.SDF.format(ca.getTime());// get today's date

        SharedPreferences sp_day = getSharedPreferences(Constants.TODAY_DATA, Context.MODE_PRIVATE);

        // get containg date by "today_date" key
        saved_date = sp_day.getString("today_date", "empty");

        //Log.e(TAG,saved_date);

        //check today's date
        if (saved_date.equals(tDate)) {
            //get today's saved data
            //saved_totalData = sp_day.getLong("TOTAL_DATA", 0);
            saved_mobileData = sp_day.getLong("MOBILE_DATA", 0);
            saved_wifiData = sp_day.getLong("WIFI_DATA", 0);

            SharedPreferences.Editor day_editor = sp_day.edit();
            // editor.putString("today", tDate);

            //update data
            //day_editor.putLong("TOTAL_DATA", totalData + saved_totalData);
            day_editor.putLong("MOBILE_DATA", mobileData + saved_mobileData);
            day_editor.putLong("WIFI_DATA", wifiData + saved_wifiData);

            day_editor.apply();

            //Log.e("today", Long.toString(saved_totalData + totalData));
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                //.put("today_data",sharedpreferences.getString("today_date",null));
                //save today's total data as a json object
                jsonObject.put("WIFI_DATA", sp_day.getLong("WIFI_DATA", 0));
                jsonObject.put("MOBILE_DATA", sp_day.getLong("MOBILE_DATA", 0));
                //jsonObject.put("TOTAL_DATA", sp_day.getLong("TOTAL_DATA", 0));

                SharedPreferences sp_month = getSharedPreferences(Constants.MONTH_DATA, Context.MODE_PRIVATE);
                SharedPreferences.Editor month_editor = sp_month.edit();

                // previous day's data save to monthdata preference
                month_editor.putString(saved_date, jsonObject.toString());
                month_editor.apply();

                SharedPreferences.Editor day_editor = sp_day.edit();

                //update new date by tDate
                day_editor.clear();
                day_editor.putString("today_date", tDate);
                day_editor.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    void startForegroundCompat(int id, Notification notification) {

        if(notification == null){
            stopForegroundCompat(id);
            return;
        }

        // If we have the new startForeground API, then use it.
        if (startForeground != null) {
            Object[] startForegroundArgs = new Object[2];
            startForegroundArgs[0] = Integer.valueOf(id);
            if (notification == null) {
                Notification notification2 = new Notification();
                startForegroundArgs[1] = notification2;
            } else {
                startForegroundArgs[1] = notification;
            }

            try {
                startForeground.invoke(this, startForegroundArgs);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        }

        // Fall back on the old API.
        Object[] setForegroundArgs = new Object[1];
        setForegroundArgs[0] = Boolean.TRUE;
        try {
            setForeground.invoke(this, setForegroundArgs);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if (notification != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification);
        }
    }

    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    void stopForegroundCompat(int id) {
        // If we have the new stopForeground API, then use it.
        if (stopForeground != null) {
            Object[] stopForegroundArgs = new Object[1];
            stopForegroundArgs[0] = Boolean.TRUE;

            try {
                stopForeground.invoke(this, stopForegroundArgs);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        }

        // Fall back on the old API. Note to cancel BEFORE changing the
        // foreground state, since we could be killed at that point.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
        Object[] setForegroundArgs = new Object[1];
        setForegroundArgs[0] = Boolean.FALSE;
        try {
            setForeground.invoke(this, setForegroundArgs);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
         }
    }
}

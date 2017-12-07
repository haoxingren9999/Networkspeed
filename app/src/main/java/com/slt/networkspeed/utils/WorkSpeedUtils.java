package com.slt.networkspeed.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.slt.networkspeed.MyApp;
import com.slt.networkspeed.R;



public class WorkSpeedUtils {
    /**
     * 返回刷新频率时间
     *
     * @param context
     * @return
     */
    public static int getFreshTime(Context context) {
        String time = SharedPrefsUtil.getValue(context, SharedPrefsUtil.SETTING, SharedPrefsUtil.FRESH_TIME, context.getString(R.string.one_sec));

        int timeIndex = 1000;
//        Log.e("123", "time " + time);
        if (time.equals(context.getString(R.string.one_point_five_sec))) {
            timeIndex = 1500;
        } else if (time.equals(context.getString(R.string.two_sec))) {
            timeIndex = 2000;
        }

        return timeIndex;
    }

    /**
     * GA页面统计
     */
    public static void sendPageStaticsForGA(String screenName) {
        Tracker mTracker = MyApp.getDefaultTracker();
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /**
     * GA点击事件统计
     */
    public static void sendClickStaticsForGA(String category, String action, String label) {
        Tracker mTracker = MyApp.getDefaultTracker();
        if (TextUtils.isEmpty(label)) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)   //类别
                    .setAction(action)//操作
                    .build());
        } else {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)   //类别
                    .setAction(action)//操作
                    .setLabel(label)
                    .build());
        }
    }

    /**
     * 向GA上报异常
     * @param context
     * @param e
     */
    public static void sendExceptiontoServer(Context context, Throwable e) {

        Tracker mTracker = MyApp.getDefaultTracker();
        mTracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(Log.getStackTraceString(e))
                .setFatal(false)
                .build());
//        new StandardExceptionParser(context, null)
//                .getDescription(Thread.currentThread().getName(), e)
    }

//    public static void sendExceptiontoServer(Context mContext, String title, Exception e) {
//        try {
//            Tracker mTracker = MyApp.getDefaultTracker();
//            mTracker.send(MapBuilder.createException(new StandardExceptionParser(mContext, null).getDescription(title + " : " + Thread.currentThread().getName(), e), false).
//                    build());
//        } catch (Exception ex) {
//        }
//    }
}

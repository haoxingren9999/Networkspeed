package com.slt.networkspeed;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

import com.google.android.gms.analytics.ExceptionParser;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import com.slt.networkspeed.bean.Package;
import com.slt.networkspeed.utils.PackageUtils;


public class MyApp extends Application {
    public static ArrayList<Package> list;
    public  static float flow;
    private static Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link android.app.Application}.
     *
     * @return tracker
     */
    synchronized public static Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(instance);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
//            mTracker.enableExceptionReporting(true);

        }
        return mTracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        exceptionReporter();
        list = PackageUtils.getPackagesData(getApplicationContext());
        flow=0;
    }

    private static MyApp instance;

    public static MyApp getInstance() {

        return instance;

    }

    private void exceptionReporter() {
        Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                getDefaultTracker(),
                Thread.getDefaultUncaughtExceptionHandler(),
                this);

        ExceptionReporter exceptionReporter = (ExceptionReporter) myHandler;
        exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
        Thread.setDefaultUncaughtExceptionHandler(exceptionReporter);


    }
    public class AnalyticsExceptionParser implements ExceptionParser {
        @Override
        public String getDescription(String thread, Throwable throwable) {
            return String.format("Thread: %s, Exception: %s", thread,Log.getStackTraceString(throwable));
        }
    }
//    class  SpeedExceptionReporter extends ExceptionReporter implements  ExceptionParser{
//        private Context mContext;
//        private Tracker mTracker;
//        private  Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;
//        @Override
//        public void setExceptionParser(ExceptionParser exceptionParser) {
//            super.setExceptionParser(exceptionParser);
//        }
//
//        @Override
//        public ExceptionParser getExceptionParser() {
//            return super.getExceptionParser();
//        }
//
//        public SpeedExceptionReporter(Tracker tracker, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, Context context){
//            mContext= context;
//            mTracker=tracker;
//            mUncaughtExceptionHandler=uncaughtExceptionHandler;
//        }
//
//        @Override
//        public void uncaughtException(Thread thread, Throwable throwable) {
//            super.uncaughtException(thread, throwable);
//        }
//
//        public Thread.UncaughtExceptionHandler getmUncaughtExceptionHandler() {
//            return mUncaughtExceptionHandler;
//        }
//
//        public void setmUncaughtExceptionHandler(Thread.UncaughtExceptionHandler mUncaughtExceptionHandler) {
//            this.mUncaughtExceptionHandler = mUncaughtExceptionHandler;
//        }
//
//        @Override
//        public String getDescription(String s, Throwable throwable) {
//            return null;
//        }
//    }
}




package com.slt.networkspeed.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


public class NetworkUtil {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    /**
     * @param context
     * @return Connection Type which is Wifi or Mobile or No Connection
     */

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }

        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = "wifi_enabled";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = "mobile_enabled";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = "no_connection";
        }
        return status;
    }

    public static List<String> getConnectivityInfo(Context context) {

        List<String> connInfo = new ArrayList<>();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        String network_status = getConnectivityStatusString(context);

        // if (null != activeNetwork) {
        if (network_status == "wifi_enabled") {
            connInfo.add("wifi_enabled");

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();

            int rssi = info.getRssi();
            int level = WifiManager.calculateSignalLevel(rssi, 10);
            int percentage = (int) ((level / 9.0) * 100);

            connInfo.add(info.getSSID());
            connInfo.add(Integer.toString(percentage) + " %");

            return connInfo;
        } else if (network_status.equals("mobile_enabled")) {
            connInfo.add("mobile_enabled");
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            connInfo.add(manager.getNetworkOperatorName());

            return connInfo;
        } else {
            connInfo.add("no_connection");
            return connInfo;
        }
    }
    public static String getWifiName(Context context){
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if(wifiManager==null)
            return "";
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && !TextUtils.isEmpty(wifiInfo.getSSID())) {
            if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
                // wifiInfo.getSSID()存在为空的情况，引起空指针异常 加上非空判断 异常地址为
                // http://mobile.umeng.com/apps/84a300b229b0426585545e15/error_types/show?error_type_id=51e5455856240b922b003a48_9092714159640537595_4.5.9
                if (Build.VERSION.SDK_INT < 17) {
                    return wifiInfo.getSSID() == null ? "" : "\""+wifiInfo.getSSID()+"\"";
                } else {
                    try{
                        String wifi = wifiInfo.getSSID() == null ? "" : wifiInfo.getSSID();
//                        String wifi = wifiInfo.getSSID() == null ? "" : wifiInfo
//                                .getSSID().substring(1,
//                                        wifiInfo.getSSID().length() - 1);
                        return wifi;
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        return "";
                    }
                }
            } else {
                return "";

            }
        } else {
            return "";
        }


    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo mobileNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean networkAvailable = false;
        if (activeNetInfo != null) {
            networkAvailable = activeNetInfo.isConnected();
        }

        if (!networkAvailable && mobileNetInfo != null) {
            networkAvailable = mobileNetInfo.isConnected();
        }

        return networkAvailable;
    }

}

package com.slt.networkspeed.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;



public class Constants {
    public static final String TODAY_DATA = "todaydata";
    public static final String MONTH_DATA = "monthdata";

    //虽然只展示月和日，但是为了数据安全，避免今年的数据不至于和以后年份的数据重复，将年份也保存了下来，只是展示时将其去掉
    public static final SimpleDateFormat SDF = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);

    //进入app
    public static final int ACTION_TYPE_SHOW_APP = 1;
    //执行创建快捷方式
    public static final int ACTION_TYPE_SHORTCUT = 2;
    //执行卸载
    public static final int ACTION_TYPE_UNINSTALL = 3;

}

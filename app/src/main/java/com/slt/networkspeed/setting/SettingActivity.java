package com.slt.networkspeed.setting;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rey.material.widget.Switch;

import com.slt.networkspeed.R;
import com.slt.networkspeed.mvp.MVPBaseActivity;
import com.slt.networkspeed.ui.ClientSettingItemView;
import com.slt.networkspeed.utils.SharedPrefsUtil;
import com.slt.networkspeed.utils.WorkSpeedUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




public class SettingActivity extends MVPBaseActivity<SettingContract.View, SettingPresenter> implements SettingContract.View, View.OnClickListener {

    private ClientSettingItemView notification;
    private ClientSettingItemView autoHide;
    private RelativeLayout frechRate;
    private RelativeLayout speedUnit;
    private Spinner mFreshRateSpinner;
    private Spinner mSpeedUnitSpinner;
    private Context mContext;
    private PopupWindow popupWindowFreshRate;
    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mContext = this;
        WorkSpeedUtils.sendPageStaticsForGA("networkspeed_setings");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        initView();
//        Intent intent = new Intent(this, SpeedService.class);
//        startService(intent);

//        List<String> list = null ;
//        list.toString();
    }

    private void initView() {
        notification = (ClientSettingItemView) findViewById(R.id.tv_notification);
        autoHide = (ClientSettingItemView) findViewById(R.id.tv_auto_show_hide);
        frechRate = (RelativeLayout) findViewById(R.id.inc_fresh_rate);
        speedUnit = (RelativeLayout) findViewById(R.id.inc_speed_unit);
        findViewById(R.id.tv_tv_share).setOnClickListener(this);
        findViewById(R.id.tv_about_us).setOnClickListener(this);
        notification.setOnClickListener(this);
        autoHide.setOnClickListener(this);
        setNotification();
        setAutoHide();
        initfreshSpinner();
        initspeedSpinner();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setDelfaultSelect();
    }

    private void initspeedSpinner() {
        mSpeedUnitSpinner = (Spinner) findViewById(R.id.sp_speed_unit);
        speedUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpeedUnitSpinner.performClick();
            }
        });
        mSpeedUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //选择列表项的操作
                boolean isK = true;//字节j就为true
                if (position == 0) {
                    isK = true;
                } else {
                    isK = false;
                }
                SharedPrefsUtil.putValue(mContext, SharedPrefsUtil.SETTING, SharedPrefsUtil.SPEED_UNIT, isK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //未选中时候的操作
            }
        });
        List<String> mList = new ArrayList<String>();
        String[] s = getResources().getStringArray(R.array.speed_unit);
        Collections.addAll(mList, s);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item_select, mList);
        //传入的参数分别为 Context , 未选中项的textview , 数据源List
        //单独设置下拉的textview
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSpeedUnitSpinner.setAdapter(arrayAdapter);

    }

    private void initfreshSpinner() {
        mFreshRateSpinner = (Spinner) findViewById(R.id.sp_fresh_time);
        frechRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFreshRateSpinner.performClick();

            }
        });
        mFreshRateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //选择列表项的操作
                TextView textView = (TextView) findViewById(R.id.tv_time);
//                Log.e("123", "textView " + textView.getText().toString());
                SharedPrefsUtil.putValue(mContext, SharedPrefsUtil.SETTING, SharedPrefsUtil.FRESH_TIME, textView.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //未选中时候的操作
            }
        });
        List<String> mList = new ArrayList<String>();
        String[] s = getResources().getStringArray(R.array.fresh_rate);
        Collections.addAll(mList, s);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item_select, mList);
        //传入的参数分别为 Context , 未选中项的textview , 数据源List
        //单独设置下拉的textview
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        mFreshRateSpinner.setAdapter(arrayAdapter);

    }

    private void setDelfaultSelect() {
        String time = SharedPrefsUtil.getValue(mContext, SharedPrefsUtil.SETTING, SharedPrefsUtil.FRESH_TIME, getString(R.string.one_sec));
        boolean unit = SharedPrefsUtil.getValue(mContext, SharedPrefsUtil.SETTING, SharedPrefsUtil.SPEED_UNIT, true);
        int timeIndex = 0;
        int unitIndex = 0;
//        Log.e("123", "time " + time);
        if (time.equals(getString(R.string.one_point_five_sec))) {
            timeIndex = 1;
        } else if (time.equals(getString(R.string.two_sec))) {
            timeIndex = 2;
        }
        if (unit) {
            unitIndex = 0;
        }else{
            unitIndex = 1;
        }

        if (mFreshRateSpinner != null) mFreshRateSpinner.setSelection(timeIndex);
        if (mSpeedUnitSpinner != null) mSpeedUnitSpinner.setSelection(unitIndex);

    }

    private void setNotification() {
        boolean notificationEnable = SharedPrefsUtil.getValue(mContext, SharedPrefsUtil.SETTING, SharedPrefsUtil.NOTIFICATION_SWITCH, true);//读取xml中的值 设置是否开关
        if (!notificationEnable) {
            autoHide.getSwitch().setChecked(false);
            autoHide.setClickable(false);
            autoHide.getSwitch().setClickable(false);
        }
        notification.getSwitch().setChecked(notificationEnable);
        notification.setOnClickListener(this);
        notification.getSwitch().setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                notification.getSwitch().setChecked(checked);
                if (!checked) {

                    autoHide.setClickable(false);
                    autoHide.getSwitch().applyStyle(R.style.DarkSwitch);
//                    SharedPrefsUtil.putValue(mContext, SharedPrefsUtil.SETTING, SharedPrefsUtil.AUTOHIDE, false);
                } else {
                    autoHide.setClickable(true);
                    autoHide.getSwitch().setClickable(true);
                    autoHide.getSwitch().applyStyle(R.style.LightSwitch);
                }
                //存入文件
                SharedPrefsUtil.putValue(mContext, SharedPrefsUtil.SETTING, SharedPrefsUtil.NOTIFICATION_SWITCH, checked);
            }
        });
    }

    private void setAutoHide() {
        boolean autoHideEnable = SharedPrefsUtil.getValue(mContext, SharedPrefsUtil.SETTING, SharedPrefsUtil.AUTOHIDE, false);
       //读取xml中的值 设置是否开关
//        Log.e("123","autoHideEnable "+autoHideEnable);
        autoHide.getSwitch().setChecked(autoHideEnable);
        autoHide.setOnClickListener(this);
        autoHide.getSwitch().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean notificationEnable = SharedPrefsUtil.getValue(mContext, SharedPrefsUtil.SETTING, SharedPrefsUtil.NOTIFICATION_SWITCH, true);
                if(notificationEnable){
                    return false;
                }else{

                    return true;
                }
            }
        });
        autoHide.getSwitch().setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if (checked) {
                    SharedPrefsUtil.putValue(getApplicationContext(), SharedPrefsUtil.SETTING, SharedPrefsUtil.IS_NOT_0, System.currentTimeMillis());
                }
                SharedPrefsUtil.putValue(mContext, SharedPrefsUtil.SETTING, SharedPrefsUtil.AUTOHIDE, checked);

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_notification:
                notification.getSwitch().toggle();
                break;
            case R.id.tv_auto_show_hide:
                autoHide.getSwitch().toggle();
                break;


            case R.id.tv_tv_share:
                share();
                break;
            case R.id.tv_about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;


        }
    }

    private void feedBack() {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse("https://www.facebook.com/easytouch1/");
            intent.setData(content_url);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(notification, this.getString(R.string.error_starting_activity_unknow), Snackbar.LENGTH_LONG).show();
        }
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_content));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.share)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

package com.slt.networkspeed.main;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.slt.networkspeed.R;
import com.slt.networkspeed.adapter.MonthDataAdapter;
import com.slt.networkspeed.bean.DataInfo;
import com.slt.networkspeed.mvp.MVPBaseActivity;
import com.slt.networkspeed.service.SpeedService;
import com.slt.networkspeed.setting.SettingActivity;
import com.slt.networkspeed.ui.SpacesItemDecoration;
import com.slt.networkspeed.utils.DensityUtil;
import com.slt.networkspeed.utils.WorkSpeedUtils;

import java.util.List;



public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View {
    //持有一个prsenter对象--mPresenter
    public static final String TAG = "MainActivity";

    public TextView tvTodayDate;

    public TextView tvTodayMobile;
    public TextView tvTodayWifi;
    public TextView tvTodayTotal;

    public TextView tvTodayMobileUnit;
    public TextView tvTodayWifiUnit;
    public TextView tvTodayTotalUnit;

    public RecyclerView recyclerView;
    public MonthDataAdapter dataAdapter;

    List<DataInfo> monthData;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            progressDialog=new ProgressDialog(this,R.style.theme_progressbar);
            progressDialog.setTitle("");
            progressDialog.setMessage("Initializing");
            progressDialog.show();

            Window window = progressDialog.getWindow();
            if(window!=null){
                View view=window.getDecorView();
                if(view!=null){
                    view.setPadding(DensityUtil.dip2px(this,20), 0, DensityUtil.dip2px(this,20), 0); //消除边距
                    WindowManager.LayoutParams lp = window.getAttributes();
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    window.setAttributes(lp);
                }
            }
        }else{
            progressDialog = ProgressDialog.show(this, "", "Initializing");
        }
        progressDialog.setCanceledOnTouchOutside(true);

        WorkSpeedUtils.sendPageStaticsForGA("networkspeed_datausage");
        Toolbar toolbar = (Toolbar)findViewById(R.id.today_toolbar);
        setSupportActionBar(toolbar);

        initView();
        Log.i(TAG, "----onCreate----");
        mPresenter.initModel();

        //利用presenter层实现具体业务
        //获取30日的流量信息
        monthData = mPresenter.createList(30);
        Log.i(TAG, "----onCreate----monthData = "+monthData.toString());
        dataAdapter = new MonthDataAdapter(monthData,this);
        recyclerView.setAdapter(dataAdapter);

        //清除30之外的数据，但这个方法有点小的漏斗，如果天数大于1000,没有使用过，就无法清除1000天以前的数据，实际可能不太可能发生1000天不用的情况
        mPresenter.clearExtraData();

        //显示今日的流量数据
        mPresenter.setTodayView();

        //启动线程刷新列表
        mPresenter.loopRefreshList();

        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        if (!SpeedService.service_status) {
            Intent intent = new Intent(this, SpeedService.class);
            startService(intent);
        }
    }

    private void initView(){
        tvTodayDate = (TextView)findViewById(R.id.today_date);

        tvTodayMobile = (TextView)findViewById(R.id.today_mobile_flowdata);
        tvTodayWifi = (TextView)findViewById(R.id.today_wifi_flowdata);
        tvTodayTotal = (TextView)findViewById(R.id.today_total_flowdata);

        tvTodayMobileUnit = (TextView)findViewById(R.id.today_mobile_unit);
        tvTodayWifiUnit = (TextView)findViewById(R.id.today_wifi_unit);
        tvTodayTotalUnit = (TextView)findViewById(R.id.today_total_unit);

        recyclerView = (RecyclerView)findViewById(R.id.rv_month_flowdata);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.getItemAnimator().setChangeDuration(0);

        int spacingInPixels = 3;
        if (Build.VERSION.SDK_INT >= 21) {
            spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycleview_item_spacing1);
        } else {
            spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycleview_item_spacing2);
        }
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));



        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_main_setting){
            Intent intent = new Intent();
            intent.setClass(this, SettingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTodayViewData(List<String> list) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "----onResume---- ");
        mPresenter.modifyThreadName("started");
        mPresenter.restartThread();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "----onResume----");
        mPresenter.modifyThreadName("stopped");
    }

    public  int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

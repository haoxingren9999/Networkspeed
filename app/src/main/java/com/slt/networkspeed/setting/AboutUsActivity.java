package com.slt.networkspeed.setting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;


import com.slt.networkspeed.R;
import com.slt.networkspeed.ui.ClientSettingItemView;

import java.util.List;


public class AboutUsActivity extends AppCompatActivity   {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us_453);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //设置箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //隐藏箭头设置icon图标在最左边
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setTitle(getResources().getString(R.string.about_us));

        initView();
    }


    private void initView() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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








    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}

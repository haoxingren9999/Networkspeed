package com.slt.networkspeed.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.slt.networkspeed.service.SpeedService;



public class ListenerReceivers extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            if (!SpeedService.service_status) {
                Intent startIntent = new Intent(context, SpeedService.class);
                context.startService(startIntent);
            }
        }
    }
}

package com.example.porfeature;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service_intent = new Intent(context, VibratingService.class);
        context.startService(service_intent);
    }
}

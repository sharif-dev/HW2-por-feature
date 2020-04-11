package com.example.porfeature;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmVibrateIntent = new Intent(context, AlarmVibrateActivity.class);
        alarmVibrateIntent.putExtra(context.getString(R.string.min_z_speed),
                intent.getFloatExtra(context.getString(R.string.min_z_speed), 0.2f));
        context.startActivity(alarmVibrateIntent);
    }
}

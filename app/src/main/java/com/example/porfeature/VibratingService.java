package com.example.porfeature;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;

public class VibratingService extends Service {
    private final int MAX_VIBRATION_TIME = 10 * 60 * 1000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(MAX_VIBRATION_TIME);
        return START_NOT_STICKY;
    }
}

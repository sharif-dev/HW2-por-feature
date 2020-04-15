package com.example.porfeature;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Objects;

public class SmartLockService extends Service implements SensorEventListener {

    private ComponentName cn;
    private DevicePolicyManager mgr;
    private float mLastX, mLastY, mLastZ;

    public SmartLockService() {
        cn = MainActivity.getCn();
        mgr = MainActivity.getMgr();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SensorManager mSensorManager = MainActivity.getSensor();
        Sensor mAccelerometer = MainActivity.getAccelerometer();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (mgr.isAdminActive(cn)){
            if (mLastZ > 7)
                mgr.lockNow();
        }
        else
            Toast.makeText(getApplicationContext(),"no admin",
                    Toast.LENGTH_SHORT).show();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mLastX = x;
        mLastY = y;
        mLastZ = z;
    }

    @Override
    public void onAccuracyChanged (Sensor sensor,int accuracy){

    }

}

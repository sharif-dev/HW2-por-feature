package com.example.porfeature;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;


public class SmartLockService extends Service implements SensorEventListener {

    private ComponentName cn;
    private DevicePolicyManager mgr;
    private SensorManager sensorManager;
    float[] g = new float[3];
    private int inclination;


    public SmartLockService() {
        cn = MainActivity.getCn();
        mgr = MainActivity.getMgr();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int minDegree = intent.getIntExtra("degree", 25);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.unregisterListener(this);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        if (inclination < minDegree || inclination > 180 - minDegree){
            if (mgr.isAdminActive(cn))
                mgr.lockNow();
            else
                Toast.makeText(getApplicationContext(),"no admin",
                    Toast.LENGTH_SHORT).show();}
        else
            Toast.makeText(getApplicationContext(),"not on surface",
                    Toast.LENGTH_SHORT).show();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        g = event.values.clone();
        double norm_Of_g = Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);

        g[0] = (float) (g[0] / norm_Of_g);
        g[1] = (float) (g[1] / norm_Of_g);
        g[2] = (float) (g[2] / norm_Of_g);

        inclination = (int) Math.round(Math.toDegrees(Math.acos(g[2])));
    }

    @Override
    public void onAccuracyChanged (Sensor sensor,int accuracy){

    }


}

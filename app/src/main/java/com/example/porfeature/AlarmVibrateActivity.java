package com.example.porfeature;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AlarmVibrateActivity extends Activity implements SensorEventListener {
    private final int VIBRATION_TIME = 10 * 60 * 1000;
    private final int VIBRATION_AMPLITUDE = 10;

    private float minZSpeed;
    private Vibrator vibrator;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        minZSpeed = getIntent().getFloatExtra(getString(R.string.min_z_speed), 0.2f);

        setContentView(R.layout.activity_vibrate_alarm);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_TIME, VIBRATION_AMPLITUDE));
            } else {
                vibrator.vibrate(VIBRATION_TIME);
            }
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float zRotationalSpeed = Math.abs(event.values[2]);
        if (zRotationalSpeed > minZSpeed) {
            vibrator.cancel();
            sensorManager.unregisterListener(this);
            Toast.makeText(this, "Hooray!! Deactivated!!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

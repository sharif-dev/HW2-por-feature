package com.example.porfeature;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    PendingIntent alarmPendingIntent;
    AlarmManager alarmManager;
    private static DevicePolicyManager mgr;
    private static ComponentName cn;
    private static SensorManager mSensorManager;
    private static Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = Objects.requireNonNull(mSensorManager).getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        setListeners();
        TimePicker timePicker = findViewById(R.id.alarm_time_picker);
        timePicker.setIs24HourView(true);
    }

    public static Sensor getAccelerometer(){
        return mAccelerometer;
    }

    public static SensorManager getSensor(){
        return mSensorManager;
    }

    public static DevicePolicyManager getMgr(){
        return mgr;
    }

    public static ComponentName getCn(){
        return cn;
    }

    private void setListeners() {
        setAlarmCheckBoxListener();
        setAlarmButtonListener();
        setLockCheckBoxListener();
    }

    private void setLockCheckBoxListener() {
        CheckBox lockCheckBox = findViewById(R.id.set_unlock);
        lockCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onSetLockFeature();
                } else {
                    cancelLockFeature();
                }
            }
        });
    }

    private void cancelLockFeature() {
        Intent intent = new Intent(MainActivity.this, SmartLockService.class);
        stopService(intent);
    }

    private void onSetLockFeature() {
        mgr = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        cn = new ComponentName(this, Admin.class);
        Intent intent_2 = new Intent(MainActivity.this, SmartLockService.class);
        if (mgr.isAdminActive(cn))
            startService(intent_2);
        else{
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                R.string.device_admin_explanation);
        startActivityForResult(intent, 0);}
    }

    private void setAlarmCheckBoxListener() {
        CheckBox alarmCheckBox = findViewById(R.id.set_alarm_checkbox);
        alarmCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onSetAlarmClick();
                } else {
                    cancelAlarm();
                }
            }
        });
    }

    private void setAlarmButtonListener() {
        Button button = findViewById(R.id.set_alarm_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox alarmCheckBox = findViewById(R.id.set_alarm_checkbox);
                alarmCheckBox.setChecked(true);
            }
        });
    }

    private void onSetAlarmClick() {
        TimePicker picker = findViewById(R.id.alarm_time_picker);
        int hour, minute;
        if (Build.VERSION.SDK_INT >= 23) {
            hour = picker.getHour();
            minute = picker.getMinute();
        } else {
            hour = picker.getCurrentHour();
            minute = picker.getCurrentMinute();
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        scheduleAlarm(calendar);
    }

    private void scheduleAlarm(Calendar calendar) {
        EditText minZ = findViewById(R.id.edit_text_min_z_speed);
        float minZSpeed;
        try {
            minZSpeed = Float.parseFloat(minZ.getText().toString());
        } catch (NumberFormatException e) {
            minZSpeed = 0.2f;
        }

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.putExtra(getString(R.string.min_z_speed), minZSpeed);
        alarmPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
    }

    private void cancelAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(alarmPendingIntent);
        }
    }
}

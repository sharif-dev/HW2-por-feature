package com.example.porfeature;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {
    PendingIntent alarmPendingIntent;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListeners();

        TimePicker timePicker = findViewById(R.id.alarm_time_picker);
        timePicker.setIs24HourView(true);
    }

    private void setListeners() {
        setAlarmCheckBoxListener();
        setAlarmButtonListener();
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

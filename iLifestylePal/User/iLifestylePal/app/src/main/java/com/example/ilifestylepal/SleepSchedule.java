package com.example.ilifestylepal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SleepSchedule extends AppCompatActivity {

    private LinearLayout ll_viewSleepRecord;
    private LinearLayout linear_sleepingTime;
    private LinearLayout linear_wakingTime;
    private TextView tv_sleepingTime;
    private TextView tv_wakingTime;

    private TextView tv_sleepDuration;
    private TextView tv_dateToday, tv_sleepAt, tv_wakeUpAt;
    private ProgressBar pb_sleepProgress;

    private PendingIntent pendingIntent, pendingIntent2;
    private AlarmManager alarmManager, alarmManager2;

    private SharedPreferences sharedPreferences;

    private FirebaseAuth mAuth;
    private DatabaseReference SleepScheduleRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_schedule2);

        //Set Up Toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.sleepSchedule_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sleep Schedule");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase Instance
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        SleepScheduleRef = FirebaseDatabase.getInstance().getReference().child("Sleep Schedule");

        //Fetch Data From SharedPreferences
        sharedPreferences = getSharedPreferences("SleepSchedule", Context.MODE_PRIVATE);

        //Bind Views
        ll_viewSleepRecord = findViewById(R.id.ll_viewSleepRecord);
        tv_sleepingTime = findViewById(R.id.tv_sleepingTime);
        tv_wakingTime = findViewById(R.id.tv_wakingTime);
        linear_sleepingTime = findViewById(R.id.linear_sleepingTime);
        linear_wakingTime = findViewById(R.id.linear_wakingTime);
        tv_sleepDuration = findViewById(R.id.tv_sleepDuration);
        tv_dateToday = findViewById(R.id.tv_dateToday);
        tv_sleepAt = findViewById(R.id.tv_sleepAt);
        tv_wakeUpAt = findViewById(R.id.tv_wakeUpAt);
        pb_sleepProgress = findViewById(R.id.pb_sleepProgress);

        //Fetch Latest Sleep Duration Data of the Current User
        FetchLatestSleepScheduleData();

        //Create Notification Channel
        CreateNotificationChannel();

        //Set Alarm Trigger Default
        SetAlarm(2);

        //Go To Sleep Records Activity
        ll_viewSleepRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sleepRecords = new Intent(SleepSchedule.this, SleepRecord.class);
                startActivity(sleepRecords);
            }
        });

        //Set Sleeping Time Trigger
        tv_sleepingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetSleepingTime();
            }
        });

        //Set Sleep In Time Trigger
        tv_wakingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetWakingTime();
            }
        });

        //Start Sleep Duration Countdown
        linear_sleepingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cancel Previous Sleeping Time Alarm
                alarmManager.cancel(pendingIntent);
                alarmManager2.cancel(pendingIntent2);
                // Start SleepScheduleService with action "START"
                Intent intent = new Intent(SleepSchedule.this, SleepScheduleService.class);
                intent.setAction("START");
                startService(intent);
                //Display Message
                Toast.makeText(SleepSchedule.this, "Sleep Started", Toast.LENGTH_SHORT).show();
            }
        });

        //Stop Sleep Duration Countdown
        linear_wakingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cancel Previous Waking Time Alarm
                alarmManager.cancel(pendingIntent);
                alarmManager2.cancel(pendingIntent2);
                // Start SleepScheduleService with action "STOP"
                Intent intent = new Intent(SleepSchedule.this, SleepScheduleService.class);
                intent.setAction("STOP");
                startService(intent);
                //Display Message
                Toast.makeText(SleepSchedule.this, "Sleep Stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void FetchLatestSleepScheduleData() {
        Query query = SleepScheduleRef.orderByChild("sleep_uid").equalTo(currentUserID);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot latestChild = null;
                long latestTimestamp = 0;

                for (DataSnapshot child : snapshot.getChildren()) {
                    long timestamp = child.child("start_timestamp").getValue(Long.class);

                    if (timestamp > latestTimestamp) {
                        latestChild = child;
                        latestTimestamp = timestamp;
                    }
                }

                if (latestChild != null) {
                    // Use the latestChild variable to access the data for the latest child node
                    long startTimestamp = latestChild.child("start_timestamp").getValue(Long.class);
                    long endTimestamp = latestChild.child("end_timestamp").getValue(Long.class);
                    long sleepDuration = latestChild.child("sleep_duration").getValue(Long.class);
                    String sleepWeekday = latestChild.child("sleep_weekday").getValue(String.class);
                    int sleepDay = latestChild.child("sleep_day").getValue(Integer.class);
                    String sleepMonth = latestChild.child("sleep_month").getValue(String.class);
                    int sleepYear = latestChild.child("sleep_year").getValue(Integer.class);

                    // Set Text For Day Today
                    tv_dateToday.setText(sleepWeekday + ", " + sleepMonth + " " + sleepDay + " " + sleepYear);

                    // SetText For Sleep At
                    Calendar sleepAtCal = Calendar.getInstance();
                    sleepAtCal.setTimeInMillis(startTimestamp);
                    sleepAtCal.setTimeZone(TimeZone.getDefault());
                    String formattedTime = DateFormat.format("hh:mm aa", sleepAtCal).toString();
                    tv_sleepAt.setText(formattedTime);

                    // SetText For Wake Up At
                    Calendar wakeUpAtCal = Calendar.getInstance();
                    wakeUpAtCal.setTimeInMillis(endTimestamp);
                    wakeUpAtCal.setTimeZone(TimeZone.getDefault());
                    String formattedTime2 = DateFormat.format("hh:mm aa", wakeUpAtCal).toString();
                    tv_wakeUpAt.setText(formattedTime2);

                    // Set Text For Sleep Duration
                    long hours = sleepDuration / 60;
                    long minutes = sleepDuration % 60;
                    String formattedHours = String.format("%d h", hours);
                    String formattedMinutes = String.format("%d m", minutes);
                    tv_sleepDuration.setText(formattedHours + " " + formattedMinutes);

                    // Set Progress Bar
                    float floatProgress = ((float) sleepDuration / (float) (60 * 8)) * 100;
                    int progress = (int) floatProgress;
                    pb_sleepProgress.setProgress(progress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Sleep Schedule", error.getMessage());
            }
        });
    }

    private void CreateNotificationChannel() {
        // Create a notification channel for Android 8.0 (API level 26) and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("SleepScheduleChannel", "Sleep Schedule", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void SetSleepingTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(SleepSchedule.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Save Hour and Minute to Local Device
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("SleepingHour", hourOfDay);
                editor.putInt("SleepingMinute", minute);
                editor.apply();

                //Set Job Scheduler For Stay Up
                SetAlarm(0);
            }
        }, 20, 0, false);
        timePickerDialog.show();
    }

    private void SetWakingTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(SleepSchedule.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Save Hour and Minute to Local Device
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("WakingHour", hourOfDay);
                editor.putInt("WakingMinute", minute);
                editor.apply();

                //Set Job Scheduler For Stay Up
                SetAlarm(1);
            }
        }, 6, 0, false);
        timePickerDialog.show();
    }

    private void SetAlarm(int i) {
        int SleepingHour = sharedPreferences.getInt("SleepingHour", 20);
        int SleepingMinute = sharedPreferences.getInt("SleepingMinute", 0);
        int WakingHour = sharedPreferences.getInt("WakingHour", 6);
        int WakingMinute = sharedPreferences.getInt("WakingMinute", 0);

        // Handle selected time for Sleeping Time
        Calendar cal1 = Calendar.getInstance();
        cal1.set(0,0,0,SleepingHour,SleepingMinute, 0);
        long selectedTimeInMillis1 = cal1.getTimeInMillis();

        // Handle selected time for Waking Time
        Calendar cal2 = Calendar.getInstance();
        cal2.set(0,0,0,WakingHour,WakingMinute, 0);
        long selectedTimeInMillis2 = cal2.getTimeInMillis();

        if (i == 0) {
            SetSleepingTimeAlarm(selectedTimeInMillis1);
            tv_sleepingTime.setText(DateFormat.format("hh:mm aa", cal1));
        } else if (i == 1) {
            SetWakingTimeAlarm(selectedTimeInMillis2);
            tv_wakingTime.setText(DateFormat.format("hh:mm aa", cal2));
        } else {
            SetSleepingTimeAlarm(selectedTimeInMillis1);
            tv_sleepingTime.setText(DateFormat.format("hh:mm aa", cal1));
            SetWakingTimeAlarm(selectedTimeInMillis2);
            tv_wakingTime.setText(DateFormat.format("hh:mm aa", cal2));
        }
    }

    private void SetSleepingTimeAlarm(long selectedTimeInMillis) {
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0, new Intent(this, SleepingTimeAlarmReceiver.class),PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long interval = 24 * 60 * 60 * 1000;
        long currentTimeInMillis = System.currentTimeMillis();

        // If the selected time is in the past, add a day to the selected time until it is in the future
        while (selectedTimeInMillis < currentTimeInMillis) {
            selectedTimeInMillis += interval;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 23 or above
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, selectedTimeInMillis, pendingIntent);
            Log.e("Alarm Manager", "Trigger SetExactAndAllowWhileIdle");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Below API 23 but equal to or greater than Android 5 (Lollipop, API 21 to 22)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, selectedTimeInMillis, pendingIntent);
            Log.e("Alarm Manager", "Trigger setExact");
        } else {
            // Below Android 5
            alarmManager.set(AlarmManager.RTC_WAKEUP, selectedTimeInMillis, pendingIntent);
            Log.e("Alarm Manager", "Trigger set");
        }
    }

    private void SetWakingTimeAlarm(long selectedTimeInMillis) {
        pendingIntent2 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(this, WakingTimeAlarmReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);

        long interval = 24 * 60 * 60 * 1000;
        long currentTimeInMillis = System.currentTimeMillis();

        // If the selected time is in the past, add a day to the selected time until it is in the future
        while (selectedTimeInMillis < currentTimeInMillis) {
            selectedTimeInMillis += interval;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 23 or above
            alarmManager2.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, selectedTimeInMillis, pendingIntent2);
            Log.e("Alarm Manager", "Trigger SetExactAndAllowWhileIdle");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Below API 23 but equal to or greater than Android 5 (Lollipop, API 21 to 22)
            alarmManager2.setExact(AlarmManager.RTC_WAKEUP, selectedTimeInMillis, pendingIntent2);
            Log.e("Alarm Manager", "Trigger setExact");
        } else {
            // Below Android 5
            alarmManager2.set(AlarmManager.RTC_WAKEUP, selectedTimeInMillis, pendingIntent2);
            Log.e("Alarm Manager", "Trigger set");
        }
    }
}
package com.example.ilifestylepal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SleepScheduleActivity extends AppCompatActivity {
    private TextView    tv_schedMon, tv_schedTue, tv_schedWed, tv_schedThu,
            tv_schedFri, tv_schedSat, tv_schedSun, tv_srn, tv_san;

    private Switch sw_setReminderNotification, sw_setAlarmNotification;

    private ImageButton ib_edit1, ib_edit2, ib_edit3, ib_edit4, ib_edit5, ib_edit6, ib_edit7;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, SleepScheduleRef, SleepScheduleSettingsRef;
    String currentUserID;

    String  tS1 = "", tE1 = "",
            tS2 = "", tE2 = "",
            tS3 = "", tE3 = "",
            tS4 = "", tE4 = "",
            tS5 = "", tE5 = "",
            tS6 = "", tE6 = "",
            tS7 = "", tE7 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_schedule);
        createBedtimeNotificationChannel();
        SetBedtimeNotification();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        SleepScheduleRef = FirebaseDatabase.getInstance().getReference().child("Sleep Schedule").child(currentUserID);
        SleepScheduleSettingsRef = FirebaseDatabase.getInstance().getReference().child("Sleep Schedule").child(currentUserID).child("Settings");

        SetupToolbar();

        sw_setReminderNotification = findViewById(R.id.sw_setReminderNotification);
        sw_setAlarmNotification = findViewById(R.id.sw_setAlarmNotification);

        tv_schedMon = findViewById(R.id.tv_schedMon);
        tv_schedTue = findViewById(R.id.tv_schedTue);
        tv_schedWed = findViewById(R.id.tv_schedWed);
        tv_schedThu = findViewById(R.id.tv_schedThu);
        tv_schedFri = findViewById(R.id.tv_schedFri);
        tv_schedSat = findViewById(R.id.tv_schedSat);
        tv_schedSun = findViewById(R.id.tv_schedSun);
        tv_srn = findViewById(R.id.tv_srn);
        tv_san = findViewById(R.id.tv_san);

        ib_edit1 = findViewById(R.id.ib_edit1);
        ib_edit2 = findViewById(R.id.ib_edit2);
        ib_edit3 = findViewById(R.id.ib_edit3);
        ib_edit4 = findViewById(R.id.ib_edit4);
        ib_edit5 = findViewById(R.id.ib_edit5);
        ib_edit6 = findViewById(R.id.ib_edit6);
        ib_edit7 = findViewById(R.id.ib_edit7);

        SharedPreferences sharedPreferences = getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
        boolean isReminderNotificationOn = sharedPreferences.getBoolean("reminder_status", true);
        boolean isAlarmNotificationOn = sharedPreferences.getBoolean("alarm_status", true);
        int reminder_minutes = sharedPreferences.getInt("reminder_minutes", 0);
        sw_setReminderNotification.setChecked(isReminderNotificationOn);
        sw_setAlarmNotification.setChecked(isAlarmNotificationOn);

        if(isReminderNotificationOn==true)
        {
            tv_srn.setText("Remind me " + reminder_minutes + " minutes before bedtime");
        }

        sw_setAlarmNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    AlarmNotificationIsOn();
                }else{
                    AlarmNotificationIsOff();
                }
            }
        });

        sw_setReminderNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    ReminderNotificationIsOn();
                }else{
                    ReminderNotificationIsOff();
                }
            }
        });

        ib_edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToAddSleepScheduleActivity("1",tS1,tE1);
            }
        });

        ib_edit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToAddSleepScheduleActivity("2",tS2,tE2);
            }
        });

        ib_edit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToAddSleepScheduleActivity("3",tS3,tE3);
            }
        });

        ib_edit4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToAddSleepScheduleActivity("4",tS4,tE4);
            }
        });

        ib_edit5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToAddSleepScheduleActivity("5",tS5,tE5);
            }
        });

        ib_edit6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToAddSleepScheduleActivity("6",tS6,tE6);
            }
        });

        ib_edit7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToAddSleepScheduleActivity("7",tS7,tE7);
            }
        });

        GetMondaySched();
        GetTuesdaySched();
        GetWednesdaySched();
        GetThursdaySched();
        GetFridaySched();
        GetSaturdaySched();
        GetSundaySched();

        GetSleepScheduleSettingsData();
    }

    private void SetBedtimeNotification() {
        SharedPreferences sharedPreferences = getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
        String tsMon = sharedPreferences.getString("ts1", "10:00 PM");
        String tsTue = sharedPreferences.getString("ts2", "10:00 PM");
        String tsWed = sharedPreferences.getString("ts3", "10:00 PM");
        String tsThu = sharedPreferences.getString("ts4", "10:00 PM");
        String tsFri = sharedPreferences.getString("ts5", "10:00 PM");
        String tsSat = sharedPreferences.getString("ts6", "10:00 PM");
        String tsSun = sharedPreferences.getString("ts7", "10:00 PM");

        Calendar cal_dow = Calendar.getInstance();
        Date date = cal_dow.getTime();
        String dow = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        String tsTime = "10:00 PM";
        int reqCode = 0;

        //What is the day today?
        if  (dow.equals("Monday"))   {
            tsTime = tsMon;
            reqCode = 0;
        } else if   (dow.equals("Tuesday"))  {
            tsTime = tsTue;
            reqCode = 1;
        } else if   (dow.equals("Wednesday")) {
            tsTime = tsWed;
            reqCode = 2;
        } else if   (dow.equals("Thursday"))  {
            tsTime = tsThu;
            reqCode = 3;
        } else if   (dow.equals("Friday"))  {
            tsTime = tsFri;
            reqCode = 4;
        } else if   (dow.equals("Saturday"))  {
            tsTime = tsSat;
            reqCode = 5;
        } else  {
            tsTime = tsSun;
            reqCode = 6;
        }

        //Convert Time String to Time Object
        SimpleDateFormat tsTimeF = new SimpleDateFormat("hh:mm aa");
        Date tsTimeD = null;
        try {
            tsTimeD = tsTimeF.parse(tsTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Get Hour and Minute
        Calendar cal_ts = Calendar.getInstance();
        cal_ts.setTime(tsTimeD);
        int h = cal_ts.get(Calendar.HOUR_OF_DAY);
        int m = cal_ts.get(Calendar.MINUTE);

        Toast.makeText(this, dow + ' ' + h + " : " + m, Toast.LENGTH_SHORT).show();
        //Set Bedtime Notification Time
        Calendar cal_bedtimeAlarm = Calendar.getInstance();
        cal_bedtimeAlarm.setTimeInMillis(System.currentTimeMillis());
        cal_bedtimeAlarm.set(Calendar.HOUR_OF_DAY, h);
        cal_bedtimeAlarm.set(Calendar.MINUTE, m);
        cal_bedtimeAlarm.set(Calendar.SECOND, 0);
        cal_bedtimeAlarm.set(Calendar.MILLISECOND, 0);

        //Set Bedtime Notification
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, BedtimeNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reqCode, intent, 0);

        alarmManager.cancel(pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal_bedtimeAlarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7,pendingIntent);
    }

    private void createBedtimeNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "BedtimeReminderChannel";
            String description = "Channel For Bedtime Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("bedtimeNotification", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void SendUserToAddSleepScheduleActivity(String s, String tS, String tE) {
        Intent add_ss = new Intent(SleepScheduleActivity.this, AddSleepSchedule.class);
        add_ss.putExtra("day", s);
        add_ss.putExtra("ts", tS);
        add_ss.putExtra("te", tE);
        startActivity(add_ss);
    }

    private void GetSleepScheduleSettingsData() {
        SleepScheduleSettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String reminderStatus = "", reminderMinutes = "";

                    if(snapshot.hasChild("reminder_status"))
                    {
                        reminderStatus = snapshot.child("reminder_status").getValue().toString();
                    }

                    if(snapshot.hasChild("reminder_minutes"))
                    {
                        reminderMinutes = snapshot.child("reminder_minutes").getValue().toString();
                    }

                    if(reminderStatus == "OFF")
                    {
                        tv_srn.setText("Reminder is Turned OFF");
                    }

                    if(reminderStatus == "_OFF")
                    {
                        sw_setReminderNotification.setChecked(false);
                        ReminderNotificationIsOff();
                    }

                    if(reminderStatus == "ON")
                    {
                        tv_srn.setText("Remind me " + reminderMinutes + " minutes before bedtime");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void AlarmNotificationIsOn() {
        HashMap settingsMap = new HashMap();
        settingsMap.put("alarm_status", "ON");

        SleepScheduleSettingsRef.updateChildren(settingsMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

            }
        });

        SharedPreferences sharedPref = getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean("alarm_status",true).apply();

        Toast.makeText(SleepScheduleActivity.this, "Alarm ON", Toast.LENGTH_SHORT).show();
        tv_srn.setText("Alarm is Turned ON");
    }

    private void AlarmNotificationIsOff() {
        HashMap settingsMap = new HashMap();
        settingsMap.put("alarm_status", "OFF");

        SleepScheduleSettingsRef.updateChildren(settingsMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

            }
        });

        SharedPreferences sharedPref = getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean("alarm_status",false).apply();

        Toast.makeText(SleepScheduleActivity.this, "Alarm OFF", Toast.LENGTH_SHORT).show();
        tv_srn.setText("Alarm is Turned OFF");
    }

    private void ReminderNotificationIsOn() {
        SchedReminderDialog setReminderNotificationDialog = new SchedReminderDialog();
        setReminderNotificationDialog.show(getSupportFragmentManager(), "SRN");
    }

    private void ReminderNotificationIsOff() {
        HashMap settingsMap = new HashMap();
        settingsMap.put("reminder_status", "OFF");

        SleepScheduleSettingsRef.updateChildren(settingsMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

            }
        });

        SharedPreferences sharedPref = getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean("reminder_status",false).apply();

        Toast.makeText(SleepScheduleActivity.this, "Reminder OFF", Toast.LENGTH_SHORT).show();
        tv_srn.setText("Reminder is Turned OFF");
    }

    private void GetMondaySched() {
        SleepScheduleRef.child("MONDAY").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.hasChild("timeStart"))
                    {
                        tS1 = snapshot.child("timeStart").getValue().toString();
                    }

                    if(snapshot.hasChild("timeEnd"))
                    {
                        tE1 = snapshot.child("timeEnd").getValue().toString();
                    }

                    tv_schedMon.setText(tS1 + " - " + tE1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetTuesdaySched() {
        SleepScheduleRef.child("TUESDAY").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.hasChild("timeStart"))
                    {
                        tS2 = snapshot.child("timeStart").getValue().toString();
                    }

                    if(snapshot.hasChild("timeEnd"))
                    {
                        tE2 = snapshot.child("timeEnd").getValue().toString();
                    }

                    tv_schedTue.setText(tS2 + " - " + tE2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetWednesdaySched() {
        SleepScheduleRef.child("WEDNESDAY").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.hasChild("timeStart"))
                    {
                        tS3 = snapshot.child("timeStart").getValue().toString();
                    }

                    if(snapshot.hasChild("timeEnd"))
                    {
                        tE3 = snapshot.child("timeEnd").getValue().toString();
                    }

                    tv_schedWed.setText(tS3 + " - " + tE3);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetThursdaySched() {
        SleepScheduleRef.child("THURSDAY").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.hasChild("timeStart"))
                    {
                        tS4 = snapshot.child("timeStart").getValue().toString();
                    }

                    if(snapshot.hasChild("timeEnd"))
                    {
                        tE4 = snapshot.child("timeEnd").getValue().toString();
                    }

                    tv_schedThu.setText(tS4 + " - " + tE4);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetFridaySched() {
        SleepScheduleRef.child("FRIDAY").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.hasChild("timeStart"))
                    {
                        tS5 = snapshot.child("timeStart").getValue().toString();
                    }

                    if(snapshot.hasChild("timeEnd"))
                    {
                        tE5 = snapshot.child("timeEnd").getValue().toString();
                    }

                    tv_schedFri.setText(tS5 + " - " + tE5);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetSaturdaySched() {
        SleepScheduleRef.child("SATURDAY").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.hasChild("timeStart"))
                    {
                        tS6 = snapshot.child("timeStart").getValue().toString();
                    }

                    if(snapshot.hasChild("timeEnd"))
                    {
                        tE6 = snapshot.child("timeEnd").getValue().toString();
                    }

                    tv_schedSat.setText(tS6 + " - " + tE6);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetSundaySched() {
        SleepScheduleRef.child("SUNDAY").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.hasChild("timeStart"))
                    {
                        tS7 = snapshot.child("timeStart").getValue().toString();
                    }

                    if(snapshot.hasChild("timeEnd"))
                    {
                        tE7 = snapshot.child("timeEnd").getValue().toString();
                    }

                    tv_schedSun.setText(tS7 + " - " + tE7);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SetupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.sleepSchedule_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sleep Schedule");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
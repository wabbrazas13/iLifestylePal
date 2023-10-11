package com.example.ilifestylepal;

import static com.example.ilifestylepal.R.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.HashMap;

public class AddSleepSchedule extends AppCompatActivity {
    private TextView tv_cancel, tv_save, tv_timeStart, tv_timeEnd, tv_1;
    private CheckBox cb_mon, cb_tue, cb_wed, cb_thu, cb_fri, cb_sat, cb_sun;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, SleepScheduleRef;
    String currentUserID;

    int tH, tM;
    boolean mon, tue, wed, thu, fri, sat, sun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_add_sleep_schedule);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        SleepScheduleRef = FirebaseDatabase.getInstance().getReference().child("Sleep Schedule").child(currentUserID);

        cb_mon = findViewById(R.id.cb_mon);
        cb_tue = findViewById(R.id.cb_tue);
        cb_wed = findViewById(R.id.cb_wed);
        cb_thu = findViewById(R.id.cb_thu);
        cb_fri = findViewById(R.id.cb_fri);
        cb_sat = findViewById(R.id.cb_sat);
        cb_sun = findViewById(R.id.cb_sun);

        tv_cancel = findViewById(R.id.tv_cancel);
        tv_save = findViewById(R.id.tv_save);
        tv_timeStart = findViewById(R.id.tv_timeStart);
        tv_timeEnd = findViewById(R.id.tv_timeEnd);
        tv_1 = findViewById(R.id.tv_1);

        tv_1.setText("Set Schedule");

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackToPreviousActivity();
            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveSleepSchedule();
                BackToPreviousActivity();
            }
        });

        tv_timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetTime("tS");
            }
        });

        tv_timeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetTime("tE");
            }
        });

        cb_mon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mon = true;
                }else{
                    mon = false;
                }
            }
        });

        cb_tue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    tue = true;
                }else{
                    tue = false;
                }
            }
        });

        cb_wed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    wed = true;
                }else{
                    wed = false;
                }
            }
        });

        cb_thu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    thu = true;
                }else{
                    thu = false;
                }
            }
        });

        cb_fri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    fri = true;
                }else{
                    fri = false;
                }
            }
        });

        cb_sat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sat = true;
                }else{
                    sat = false;
                }
            }
        });

        cb_sun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sun = true;
                }else{
                    sun = false;
                }
            }
        });

        LoadPrevData();
    }

    private void LoadPrevData() {
        String day = getIntent().getExtras().get("day").toString();
        String ts = getIntent().getExtras().get("ts").toString();
        String te = getIntent().getExtras().get("te").toString();

        tv_timeStart.setText(ts);
        tv_timeEnd.setText(te);

        if(Integer.parseInt(day) == 1)
        {
            cb_mon.setChecked(true);
            tv_1.setText("Set Schedule For MONDAY");
        }

        if(Integer.parseInt(day) == 2)
        {
            cb_tue.setChecked(true);
            tv_1.setText("Set Schedule For TUESDAY");
        }

        if(Integer.parseInt(day) == 3)
        {
            cb_wed.setChecked(true);
            tv_1.setText("Set Schedule For WEDNESDAY");
        }

        if(Integer.parseInt(day) == 4)
        {
            cb_thu.setChecked(true);
            tv_1.setText("Set Schedule For THURSDAY");
        }

        if(Integer.parseInt(day) == 5)
        {
            cb_fri.setChecked(true);
            tv_1.setText("Set Schedule For FRIDAY");
        }

        if(Integer.parseInt(day) == 6)
        {
            cb_sat.setChecked(true);
            tv_1.setText("Set Schedule For SATURDAY");
        }

        if(Integer.parseInt(day) == 7)
        {
            cb_sun.setChecked(true);
            tv_1.setText("Set Schedule For SUNDAY");
        }
    }

    private void GetTime(String tS) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AddSleepSchedule.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        tH = i;
                        tM = i1;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,tH,tM);

                        if(tS == "tS")
                        {
                            tv_timeStart.setText(DateFormat.format("hh:mm aa", calendar));
                        }

                        if(tS == "tE")
                        {
                            tv_timeEnd.setText(DateFormat.format("hh:mm aa", calendar));
                        }
                    }
                }, 12, 0, false
        );
        timePickerDialog.updateTime(tH, tM);
        timePickerDialog.show();
    }

    public void onCheckboxClicked(View view) {
        String txt1 = "", txt2 = "", txt3 = "", txt4 = "", txt5 = "", txt6 = "", txt7 = "";
        if  (mon == true)  {
            txt1 = " Mon";
        }
        if  (tue == true)  {
            txt2 = " Tue";
        }
        if  (wed == true)  {
            txt3 = " Wed";
        }
        if  (thu == true)  {
            txt4 = " Thu";
        }
        if  (fri == true)  {
            txt5 = " Fri";
        }
        if  (sat == true)  {
            txt6 = " Sat";
        }
        if  (sun == true)  {
            txt7 = " Sun";
        }

        tv_1.setText("Set Schedule For " + txt1 + txt2 + txt3 + txt4 + txt5 + txt6 + txt7);

        if  (mon == true && tue == true && wed == true && thu == true && fri == true && sat == false && sun == false)   {
            tv_1.setText("Set Schedule For WEEKDAYS");
        }
        if  (mon == false && tue == false && wed == false && thu == false && fri == false && sat == true && sun == true)   {
            tv_1.setText("Set Schedule For WEEKEND");
        }
        if  (mon == true && tue == false && wed == false && thu == false && fri == false && sat == false && sun == false)   {
            tv_1.setText("Set Schedule For MONDAY");
        }
        if  (mon == false && tue == true && wed == false && thu == false && fri == false && sat == false && sun == false)   {
            tv_1.setText("Set Schedule For TUESDAY");
        }
        if  (mon == false && tue == false && wed == true && thu == false && fri == false && sat == false && sun == false)   {
            tv_1.setText("Set Schedule For WEDNESDAY");
        }
        if  (mon == false && tue == false && wed == false && thu == true && fri == false && sat == false && sun == false)   {
            tv_1.setText("Set Schedule For THURSDAY");
        }
        if  (mon == false && tue == false && wed == false && thu == false && fri == true && sat == false && sun == false)   {
            tv_1.setText("Set Schedule For FRIDAY");
        }
        if  (mon == false && tue == false && wed == false && thu == false && fri == false && sat == true && sun == false)   {
            tv_1.setText("Set Schedule For SATURDAY");
        }
        if  (mon == false && tue == false && wed == false && thu == false && fri == false && sat == false && sun == true)   {
            tv_1.setText("Set Schedule For SUNDAY");
        }
    }

    private void SaveSleepSchedule() {
        String timeStart = tv_timeStart.getText().toString();
        String timeEnd = tv_timeEnd.getText().toString();

        HashMap monMap = new HashMap();
        monMap.put("timeStart", timeStart);
        monMap.put("timeEnd", timeEnd);

        HashMap tueMap = new HashMap();
        tueMap.put("timeStart", timeStart);
        tueMap.put("timeEnd", timeEnd);

        HashMap wedMap = new HashMap();
        wedMap.put("timeStart", timeStart);
        wedMap.put("timeEnd", timeEnd);

        HashMap thuMap = new HashMap();
        thuMap.put("timeStart", timeStart);
        thuMap.put("timeEnd", timeEnd);

        HashMap friMap = new HashMap();
        friMap.put("timeStart", timeStart);
        friMap.put("timeEnd", timeEnd);

        HashMap satMap = new HashMap();
        satMap.put("timeStart", timeStart);
        satMap.put("timeEnd", timeEnd);

        HashMap sunMap = new HashMap();
        sunMap.put("timeStart", timeStart);
        sunMap.put("timeEnd", timeEnd);

        if(mon == true)
        {
            SleepScheduleRef.child("MONDAY").updateChildren(monMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        SharedPreferences sharedPref = getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
                        sharedPref.edit().putString("ts1",timeStart).apply();
                        sharedPref.edit().putString("te1",timeEnd).apply();
                    }else{
                        Toast.makeText(AddSleepSchedule.this, "Error Occurred : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(tue == true)
        {
            SleepScheduleRef.child("TUESDAY").updateChildren(tueMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        SharedPreferences sharedPref = getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
                        sharedPref.edit().putString("ts2",timeStart).apply();
                        sharedPref.edit().putString("te2",timeEnd).apply();
                    }else{
                        Toast.makeText(AddSleepSchedule.this, "Error Occurred : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(wed == true)
        {
            SleepScheduleRef.child("WEDNESDAY").updateChildren(wedMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        SharedPreferences sharedPref = getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
                        sharedPref.edit().putString("ts3",timeStart).apply();
                        sharedPref.edit().putString("te3",timeEnd).apply();
                    }else{
                        Toast.makeText(AddSleepSchedule.this, "Error Occurred : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(thu == true)
        {
            SleepScheduleRef.child("THURSDAY").updateChildren(thuMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        SharedPreferences sharedPref = getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
                        sharedPref.edit().putString("ts4",timeStart).apply();
                        sharedPref.edit().putString("te4",timeEnd).apply();
                    }else{
                        Toast.makeText(AddSleepSchedule.this, "Error Occurred : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(fri == true)
        {
            SleepScheduleRef.child("FRIDAY").updateChildren(friMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        SharedPreferences sharedPref = getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
                        sharedPref.edit().putString("ts5",timeStart).apply();
                        sharedPref.edit().putString("te5",timeEnd).apply();
                    }else{
                        Toast.makeText(AddSleepSchedule.this, "Error Occurred : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(sat == true)
        {
            SleepScheduleRef.child("SATURDAY").updateChildren(satMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        SharedPreferences sharedPref = getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
                        sharedPref.edit().putString("ts6",timeStart).apply();
                        sharedPref.edit().putString("te6",timeEnd).apply();
                    }else{
                        Toast.makeText(AddSleepSchedule.this, "Error Occurred : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(sun == true)
        {
            SleepScheduleRef.child("SUNDAY").updateChildren(sunMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        SharedPreferences sharedPref = getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
                        sharedPref.edit().putString("ts7",timeStart).apply();
                        sharedPref.edit().putString("te7",timeEnd).apply();
                    }else{
                        Toast.makeText(AddSleepSchedule.this, "Error Occurred : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void BackToPreviousActivity() {
        Intent backIntent = new Intent(AddSleepSchedule.this, SleepScheduleActivity.class);
        startActivity(backIntent);
    }
}
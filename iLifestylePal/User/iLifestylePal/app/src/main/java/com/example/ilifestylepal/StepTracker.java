package com.example.ilifestylepal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StepTracker extends AppCompatActivity {

    private TextView tv_stepCount;
    private ProgressBar pb_stepProgress,
            pb_stepProgress1,
            pb_stepProgress2,
            pb_stepProgress3,
            pb_stepProgress4,
            pb_stepProgress5,
            pb_stepProgress6,
            pb_stepProgress7;
    private TextView tv_date;
    private int year, month, day, weekday;

    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private DatabaseReference StepTrackerRef, UsersRef;
    private String currentUserID;

    private LinearLayout ll_my_goal, ll_step_size;
    private TextView tv_step_goal, tv_step_size;
    private int savedGoal = 7000;
    private double savedStepSize = 2.30;
    private String recommendGoal;
    private int recommendStep = 5000;
    private double bmi;
    private String gender;

    private TextView tv_kcal, tv_km;
    private LinearLayout ll_stepTrackerRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_tracker);

        //Setup Toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.steptracker_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Step Tracker");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup Firebase Instance
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        StepTrackerRef = FirebaseDatabase.getInstance().getReference().child("Step Tracker");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Start Step Tracker Service
        Intent stepTrackerServiceIntent = new Intent(this, StepTrackerService.class);
        startService(stepTrackerServiceIntent);

        //Bind Views
        tv_date = findViewById(R.id.tv_date);
        tv_stepCount = findViewById(R.id.tv_stepCount);

        pb_stepProgress = findViewById(R.id.pb_stepProgress);
        pb_stepProgress1 = findViewById(R.id.pb_stepProgress1);
        pb_stepProgress2 = findViewById(R.id.pb_stepProgress2);
        pb_stepProgress3 = findViewById(R.id.pb_stepProgress3);
        pb_stepProgress4 = findViewById(R.id.pb_stepProgress4);
        pb_stepProgress5 = findViewById(R.id.pb_stepProgress5);
        pb_stepProgress6 = findViewById(R.id.pb_stepProgress6);
        pb_stepProgress7 = findViewById(R.id.pb_stepProgress7);

        pb_stepProgress.setProgress(0);
        pb_stepProgress1.setProgress(0);
        pb_stepProgress2.setProgress(0);
        pb_stepProgress3.setProgress(0);
        pb_stepProgress4.setProgress(0);
        pb_stepProgress5.setProgress(0);
        pb_stepProgress6.setProgress(0);
        pb_stepProgress7.setProgress(0);

        pb_stepProgress.setIndeterminate(false);
        pb_stepProgress1.setIndeterminate(false);
        pb_stepProgress2.setIndeterminate(false);
        pb_stepProgress3.setIndeterminate(false);
        pb_stepProgress4.setIndeterminate(false);
        pb_stepProgress5.setIndeterminate(false);
        pb_stepProgress6.setIndeterminate(false);
        pb_stepProgress7.setIndeterminate(false);

        ll_my_goal = findViewById(R.id.ll_my_goal);
        tv_step_goal = findViewById(R.id.tv_step_goal);
        ll_step_size = findViewById(R.id.ll_step_size);
        tv_step_size = findViewById(R.id.tv_step_size);
        tv_kcal = findViewById(R.id.tv_kcal);
        tv_km = findViewById(R.id.tv_km);
        ll_stepTrackerRecord = findViewById(R.id.ll_stepTrackerRecord);

        // Set Step Goal and Step Size
        sharedPreferences = getSharedPreferences("StepTracker", Context.MODE_PRIVATE);
        savedGoal = sharedPreferences.getInt("stepGoal", 7000);
        String str_stepSize = sharedPreferences.getString("stepSize", "2.30");
        savedStepSize = Double.parseDouble(str_stepSize);
        tv_step_goal.setText("Goal is set to " + savedGoal + " steps everyday.");
        tv_step_size.setText("Your step size is " + savedStepSize + " feet.");

        GetCurrentDate();
        tv_date.setText(getWeekday(weekday) + ", " + getMonth(month) + " " + day + " " + year);

        FetchUserData();
        FetchUserStepsData();

        ll_my_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedGoal = sharedPreferences.getInt("stepGoal", 7000);
                OpenMyGoalDialog();
            }
        });

        ll_step_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_stepSize = sharedPreferences.getString("stepSize", "2.30");
                savedStepSize = Double.parseDouble(str_stepSize);
                OpenStepSizeDialog();
            }
        });

        //Go To Step Tracker Records Activity
        ll_stepTrackerRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stepRecord = new Intent(StepTracker.this, StepTrackerRecord.class);
                startActivity(stepRecord);
            }
        });
    }

    private void OpenMyGoalDialog() {
        // Inflate the custom layout for the AlertDialog
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_step_goal, null);
        // Get a reference to the custom TextView
        TextView tv_step_note = customLayout.findViewById(R.id.tv_goal_note);
        TextView tv_cdc_goal = customLayout.findViewById(R.id.tv_cdc_goal);
        TextView tv_admin_goal = customLayout.findViewById(R.id.tv_admin_goal);
        TextView tv_admin_note = customLayout.findViewById(R.id.tv_admin_note);
        EditText et_stepGoal = customLayout.findViewById(R.id.et_stepGoal);
        et_stepGoal.setText(String.valueOf(savedGoal));
        tv_admin_note.setText("Based on your profile information, we recommend a daily step goal of " + recommendGoal + " steps everyday to help you maintain a healthy lifestyle. This goal is tailored to your age, gender, weight, and height, and can help you improve your overall fitness and well-being.");
        // Create the AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the click event of the positive button here
                int inputCheck = Integer.parseInt(et_stepGoal.getText().toString());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("stepGoal", inputCheck);
                editor.apply();
                tv_step_goal.setText("Goal is set to " + inputCheck + " steps everyday.");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        // Text Change listener
        et_stepGoal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                if (length > 0) {
                    // Do something with the text, for example check if it's a valid input
                    int inputCheck = Integer.parseInt(et_stepGoal.getText().toString());
                    if (recommendStep <= 7000 && inputCheck < recommendStep) {
                        tv_step_note.setText(R.string.step_goal_bad);
                        tv_step_note.setTextColor(Color.RED);
                    } else if (recommendStep > 7000 && inputCheck < 7000) {
                        tv_step_note.setText(R.string.step_goal_bad);
                        tv_step_note.setTextColor(Color.RED);
                    } else {
                        tv_step_note.setText(R.string.step_goal_okay);
                        tv_step_note.setTextColor(Color.BLUE);
                    }
                }
            }
        });
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        // Set a click listener on the custom TextView
        tv_cdc_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event of the custom TextView here
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("stepGoal", 7000);
                editor.apply();
                tv_step_goal.setText("Goal is set to 7000 steps everyday.");
                dialog.dismiss();
            }
        });
        tv_admin_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event of the custom TextView here
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("stepGoal", recommendStep);
                editor.apply();
                tv_step_goal.setText("Goal is set to " + recommendStep + " steps everyday.");
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void OpenStepSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Step Size (ft)");
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.dialog_my_profile_account3, null);
        EditText editText = customLayout.findViewById(R.id.editText);
        editText.setText(String.valueOf(savedStepSize));
        builder.setView(customLayout);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String stepSize = editText.getText().toString().trim();
                if (TextUtils.isEmpty(stepSize)) {
                    Toast.makeText(StepTracker.this, "Invalid Input.", Toast.LENGTH_SHORT).show();
                } else {
                    double myDoubleValue = Double.parseDouble(stepSize);
                    String formattedValue = String.format("%.2f", myDoubleValue);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("stepSize", formattedValue);
                    editor.apply();
                    tv_step_size.setText("Your step size is " + formattedValue + " feet.");
                    dialogInterface.dismiss();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void FetchUserData() {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("bmi")) {
                        bmi = Double.parseDouble(snapshot.child("bmi").getValue().toString().trim());
                    }
                    if (snapshot.hasChild("gender")) {
                        gender = snapshot.child("gender").getValue(String.class);
                    }
                    if (gender.equals("Male")) {
                        if (bmi < 18.5) {
                            recommendGoal = "7,000 - 10,000";
                            recommendStep = 7000;
                        } else if (bmi >= 18.5 && bmi < 24.9) {
                            recommendGoal = "7,000 - 11,000";
                            recommendStep = 7000;
                        } else if (bmi >= 24.9 && bmi < 29.9) {
                            recommendGoal = "6,000 - 10,000";
                            recommendStep = 6000;
                        } else {
                            recommendGoal = "4,000 - 8,000";
                            recommendStep = 4000;
                        }
                    } else {
                        if (bmi < 18.5) {
                            recommendGoal = "6,000 - 8,500";
                            recommendStep = 6000;
                        } else if (bmi >= 18.5 && bmi < 24.9) {
                            recommendGoal = "6,000 - 10,000";
                            recommendStep = 6000;
                        } else if (bmi >= 24.9 && bmi < 29.9) {
                            recommendGoal = "5,500 - 9,500";
                            recommendStep = 5500;
                        } else {
                            recommendGoal = "4,000 - 8,000";
                            recommendStep = 4000;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Users", error.getMessage());
            }
        });
    }

    private void FetchUserStepsData() {
        String randomKey = currentUserID + month + day + year;

        StepTrackerRef.child(randomKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long step_timestamp = snapshot.child("step_timestamp").getValue(Long.class);
                    int step_count = Integer.parseInt(snapshot.child("step_count").getValue().toString().trim());
                    int step_goal = Integer.parseInt(snapshot.child("step_goal").getValue().toString().trim());
                    String step_weekday = snapshot.child("step_weekday").getValue(String.class);

                    float floatProgress = ((float) step_count / (float) step_goal) * 100;
                    int progress = (int) floatProgress;
                    pb_stepProgress.setProgress(progress);
                    tv_stepCount.setText(String.valueOf(step_count));

                    if (step_weekday.equals("Mon")) {
                        pb_stepProgress1.setProgress(progress);
                        pb_stepProgress2.setIndeterminate(true);
                        pb_stepProgress3.setIndeterminate(true);
                        pb_stepProgress4.setIndeterminate(true);
                        pb_stepProgress5.setIndeterminate(true);
                        pb_stepProgress6.setIndeterminate(true);
                        pb_stepProgress7.setIndeterminate(true);
                        ThisWeeksData(0, step_timestamp);
                    } else if (step_weekday.equals("Tue")) {
                        pb_stepProgress2.setProgress(progress);
                        pb_stepProgress3.setIndeterminate(true);
                        pb_stepProgress4.setIndeterminate(true);
                        pb_stepProgress5.setIndeterminate(true);
                        pb_stepProgress6.setIndeterminate(true);
                        pb_stepProgress7.setIndeterminate(true);
                        ThisWeeksData(1, step_timestamp);
                    } else if (step_weekday.equals("Wed")) {
                        pb_stepProgress3.setProgress(progress);
                        pb_stepProgress4.setIndeterminate(true);
                        pb_stepProgress5.setIndeterminate(true);
                        pb_stepProgress6.setIndeterminate(true);
                        pb_stepProgress7.setIndeterminate(true);
                        ThisWeeksData(2, step_timestamp);
                    } else if (step_weekday.equals("Thu")) {
                        pb_stepProgress4.setProgress(progress);
                        pb_stepProgress5.setIndeterminate(true);
                        pb_stepProgress6.setIndeterminate(true);
                        pb_stepProgress7.setIndeterminate(true);
                        ThisWeeksData(3, step_timestamp);
                    } else if (step_weekday.equals("Fri")) {
                        pb_stepProgress5.setProgress(progress);
                        pb_stepProgress6.setIndeterminate(true);
                        pb_stepProgress7.setIndeterminate(true);
                        ThisWeeksData(4, step_timestamp);
                    } else if (step_weekday.equals("Sat")) {
                        pb_stepProgress6.setProgress(progress);
                        pb_stepProgress7.setIndeterminate(true);
                        ThisWeeksData(5, step_timestamp);
                    } else {
                        pb_stepProgress7.setProgress(progress);
                        ThisWeeksData(6, step_timestamp);
                    }

                    CalculateKCALandDISTANCE(step_count);
                } else {
                    pb_stepProgress.setProgress(0);
                    tv_stepCount.setText(String.valueOf(0));

                    CheckWhatDay();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Step Tracker", error.getMessage());
            }
        });
    }

    private void CheckWhatDay() {
        long timestamp = System.currentTimeMillis();
        if (getWeekday(weekday).equals("Mon")) {
            pb_stepProgress1.setProgress(0);
            ThisWeeksData(0, timestamp);
        } else if (getWeekday(weekday).equals("Tue")) {
            pb_stepProgress2.setProgress(0);
            ThisWeeksData(1, timestamp);
        } else if (getWeekday(weekday).equals("Wed")) {
            pb_stepProgress3.setProgress(0);
            ThisWeeksData(2, timestamp);
        } else if (getWeekday(weekday).equals("Thu")) {
            pb_stepProgress4.setProgress(0);
            ThisWeeksData(3, timestamp);
        } else if (getWeekday(weekday).equals("Fri")) {
            pb_stepProgress5.setProgress(0);
            ThisWeeksData(4, timestamp);
        } else if (getWeekday(weekday).equals("Sat")) {
            pb_stepProgress6.setProgress(0);
            ThisWeeksData(5, timestamp);
        } else {
            pb_stepProgress7.setProgress(0);
            ThisWeeksData(6, timestamp);
        }
    }

    private void CalculateKCALandDISTANCE(int step_count) {
        String str_stepSize = sharedPreferences.getString("stepSize", "2.30");
        savedStepSize = Double.parseDouble(str_stepSize);

        double distance = savedStepSize * step_count * 0.0003048;
        String formattedValue = String.format("%.2f", distance);
        tv_km.setText(formattedValue + " km");

        UsersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("weight")) {
                    double w = Double.parseDouble(snapshot.child("weight").getValue().toString().trim());
                    double kcal = 0.75 * w * distance;
                    String formattedValue2 = String.format("%.2f", kcal);
                    tv_kcal.setText(formattedValue2 + " kcal");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Users", error.getMessage());
            }
        });
    }

    private void ThisWeeksData(int i, Long step_timestamp) {
        while (i > 0) {
            // Subtract one day from the timestamp
            step_timestamp -= 86400000L; // 1 day in milliseconds
            // Get year, month, and day from the subtracted timestamp
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(step_timestamp);
            int y = cal.get(Calendar.YEAR);
            int m = cal.get(Calendar.MONTH) + 1; // Months are 0-based in Java, so add 1
            int d = cal.get(Calendar.DAY_OF_MONTH);
            int w = cal.get(Calendar.DAY_OF_WEEK);

            // Do something with the year, month, and day (e.g. print them)
            String randomKey = currentUserID + m + d + y;
            StepTrackerRef.child(randomKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        int step_count = Integer.parseInt(snapshot.child("step_count").getValue().toString().trim());
                        int step_goal = Integer.parseInt(snapshot.child("step_goal").getValue().toString().trim());
                        String step_weekday = snapshot.child("step_weekday").getValue(String.class);

                        float floatProgress = ((float) step_count / (float) step_goal) * 100;
                        int progress = (int) floatProgress;

                        if (step_weekday.equals("Mon")) {
                            pb_stepProgress1.setProgress(progress);
                        } else if (step_weekday.equals("Tue")) {
                            pb_stepProgress2.setProgress(progress);
                        } else if (step_weekday.equals("Wed")) {
                            pb_stepProgress3.setProgress(progress);
                        } else if (step_weekday.equals("Thu")) {
                            pb_stepProgress4.setProgress(progress);
                        } else if (step_weekday.equals("Fri")) {
                            pb_stepProgress5.setProgress(progress);
                        } else if (step_weekday.equals("Sat")) {
                            pb_stepProgress6.setProgress(progress);
                        } else {
                            pb_stepProgress7.setProgress(progress);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Step Tracker", error.getMessage());
                }
            });

            i--;
        }
    }

    private void GetCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1; // Month starts from 0, so add 1 to get the actual month
        day = calendar.get(Calendar.DAY_OF_MONTH);
        weekday = calendar.get(Calendar.DAY_OF_WEEK);
    }

    // Helper method to get the weekday name
    private String getWeekday(int weekday) {
        switch (weekday) {
            case Calendar.SUNDAY:
                return "Sun";
            case Calendar.MONDAY:
                return "Mon";
            case Calendar.TUESDAY:
                return "Tue";
            case Calendar.WEDNESDAY:
                return "Wed";
            case Calendar.THURSDAY:
                return "Thu";
            case Calendar.FRIDAY:
                return "Fri";
            case Calendar.SATURDAY:
                return "Sat";
            default:
                return "";
        }
    }

    private String getMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1); // Set the month in the calendar object (month starts from 0)
        // Use SimpleDateFormat to format the month name as a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
        String monthName = dateFormat.format(calendar.getTime());
        return monthName;
    }
}
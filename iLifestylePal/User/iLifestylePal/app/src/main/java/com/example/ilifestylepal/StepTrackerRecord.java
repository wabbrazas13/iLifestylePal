package com.example.ilifestylepal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class StepTrackerRecord extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private DatabaseReference StepTrackerRef, UsersRef;
    private String currentUserID;

    private RecyclerView mRecyclerView;
    private ArrayList<StepTrackerModel> mDataList;
    private StepTrackerAdapter mAdapter;

    private TextView tv_previousMonth;
    private TextView tv_totalSteps;
    private TextView tv_totalAverage;
    private TextView tv_totalCalories;
    private TextView tv_totalDistance;

    private int year, month, day, weekday;
    private double weight = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_tracker_record);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.stepTrackerRecord_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("View Records");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        StepTrackerRef = FirebaseDatabase.getInstance().getReference().child("Step Tracker");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        sharedPreferences = getSharedPreferences("StepTracker", Context.MODE_PRIVATE);

        mDataList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.rv_step_records);

        mAdapter = new StepTrackerAdapter(mDataList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tv_previousMonth = findViewById(R.id.tv_previousMonth);
        tv_totalSteps = findViewById(R.id.tv_totalSteps);
        tv_totalAverage = findViewById(R.id.tv_totalAverage);
        tv_totalCalories = findViewById(R.id.tv_totalCalories);
        tv_totalDistance = findViewById(R.id.tv_totalDistance);

        GetCurrentDate();
        tv_previousMonth.setText(getMonth(month) + " " + year);

        StepTrackerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mDataList.clear();
                int totalSteps = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    String step_uid = data.child("step_uid").getValue(String.class);
                    int step_count = data.child("step_count").getValue(Integer.class);
                    String step_month = data.child("step_month").getValue(String.class);
                    int step_year = data.child("step_year").getValue(Integer.class);

                    if (step_uid.equals(currentUserID)) {
                        StepTrackerModel myDataModel = data.getValue(StepTrackerModel.class);
                        mDataList.add(myDataModel);

                        if (step_month.equals(getMonth(month)) && step_year == year) {
                            totalSteps = totalSteps + step_count;
                        }
                    }
                }

                Collections.sort(mDataList, new Comparator<StepTrackerModel>() {
                    @Override
                    public int compare(StepTrackerModel o1, StepTrackerModel o2) {
                        long ts1 = o1.getStep_timestamp();
                        long ts2 = o2.getStep_timestamp();
                        Date d1 = new Date(ts1);
                        Date d2 = new Date(ts2);
                        Timestamp t1 = new Timestamp(d1);
                        Timestamp t2 = new Timestamp(d2);
                        return t2.compareTo(t1); // Sort in descending order
                    }
                });

                mAdapter.notifyDataSetChanged();
                mAdapter.getFilter().filter("");

                CalculatePreviousMonthData(totalSteps);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Step Tracker", error.getMessage());
            }
        });
    }

    private void CalculatePreviousMonthData(int totalSteps) {
        //Set Text for Steps
        tv_totalSteps.setText("" + totalSteps);

        //Display Average
        double averageSteps = (double) totalSteps / 30;
        tv_totalAverage.setText(String.format("%.0f", averageSteps));

        // Fetch Step Size in local device
        String str_stepSize = sharedPreferences.getString("stepSize", "2.30");
        double savedStepSize = Double.parseDouble(str_stepSize);

        // Calculate and Set Distance to Textview
        double distance = savedStepSize * totalSteps * 0.0003048;
        String formattedValue = String.format("%.2f", distance);
        tv_totalDistance.setText(formattedValue + " km");

        // Fetch weight and use it to solve for kcal
        UsersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("weight")) {
                    double w = Double.parseDouble(snapshot.child("weight").getValue().toString().trim());
                    double kcal = 0.75 * w * distance;
                    String formattedValue2 = String.format("%.2f", kcal);
                    tv_totalCalories.setText(formattedValue2 + " kcal");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Users", error.getMessage());
            }
        });
    }

    private void GetCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1; // Month starts from 0, so add 1 to get the actual month
        day = calendar.get(Calendar.DAY_OF_MONTH);
        weekday = calendar.get(Calendar.DAY_OF_WEEK);

        if (month == 1) {
            month = 12;
            year = year - 1;
        } else {
            month = month - 1;
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
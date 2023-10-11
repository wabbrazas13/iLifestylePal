package com.example.ilifestylepal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GPSStepCounter_Activity extends AppCompatActivity implements
        SensorEventListener {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private static final String TAG = "GPSStepCounter_Activity";
    private static final int REQUEST_CODE_SIGN_IN = 1;

    private TextView step_counter, step_Distance, calorie_Burned;

    private TextView monday_steps, monday_distance;
    private TextView tuesday_steps, tuesday_distance;
    private TextView wednesday_steps, wednesday_distance;
    private TextView thursday_steps, thursday_distance;
    private TextView friday_steps, friday_distance;
    private TextView saturday_steps, saturday_distance;
    private TextView sunday_steps, sunday_distance;
    private Button btnToday,btnWeek, btnMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gpsstep_counter_activity);

        initializeViews();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope("https://www.googleapis.com/auth/fitness.activity.write"))
                .requestEmail()
                .build();

        GoogleSignInClient client = GoogleSignIn.getClient(this, options);
        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);

        btnToday.setOnClickListener(view ->{
            try {
                readData("Today");
            } catch (Exception e) {
                Log.e("Tag", e.getMessage());
            }

        });
        btnWeek.setOnClickListener(view ->{
            try {
                readData("Week");
            } catch (Exception e) {
                Log.e("Tag", e.getMessage());
            }
        });
        btnMonth.setOnClickListener(view ->{
            try {
                readData("Month");
            } catch (Exception e) {
                Log.e("Tag", e.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();

                FitnessOptions fitnessOptions = FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                        .build();

                if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                    GoogleSignIn.requestPermissions(
                            this, // your activity
                            GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                            account,
                            fitnessOptions);
                } else {
                    // We have the necessary permissions, so we can access the Google Fit API
                    readData("Today");
                }
            }
        }
    }

    private long startTime, endTime;
    private String currentDateRange;

    private void readData(String dateRange) {
        Calendar cal = Calendar.getInstance();

        if(dateRange.equals("Today")){
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            startTime = cal.getTimeInMillis();
            endTime = System.currentTimeMillis();

            currentDateRange = "Today";
        }
        else if(dateRange.equals("Week")){
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
             endTime = System.currentTimeMillis();
             startTime = endTime - TimeUnit.DAYS.toMillis(7); // 7 days ago

            currentDateRange = "Week";
        }
        else if(dateRange.equals("Month")){
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
             endTime = System.currentTimeMillis();
             startTime = endTime - TimeUnit.DAYS.toMillis(30); // 30 days ago

            currentDateRange = "Month";
        }
        else {
            // Specific date range
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            try {
                Date date = format.parse(dateRange);
                cal.setTime(date);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                startTime = cal.getTimeInMillis();
                endTime = startTime + TimeUnit.DAYS.toMillis(1); // End time is one day after start time
                currentDateRange = dateRange;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {
                    // Process step count, distance, and calorie data
                    List<Bucket> buckets = dataReadResponse.getBuckets();
                    for (Bucket bucket : buckets) {
                        // Get step count data
                        DataSet stepData = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA);
                        int stepCount = stepData.isEmpty()
                                ? 0
                                : stepData.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                        // Get distance data
                        DataSet distanceData = bucket.getDataSet(DataType.AGGREGATE_DISTANCE_DELTA);
                        float distance = distanceData.isEmpty()
                                ? 0
                                : distanceData.getDataPoints().get(0).getValue(Field.FIELD_DISTANCE).asFloat() / 1000; // divide by 1000 to convert to km

                        // Get calorie data
                        DataSet calorieData = bucket.getDataSet(DataType.AGGREGATE_CALORIES_EXPENDED);
                        float calories = calorieData.isEmpty()
                                ? 0
                                : calorieData.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat();

                        // Update UI with the retrieved data
                        step_counter.setText(String.valueOf(stepCount));
                        step_Distance.setText(String.format("%.2f", distance)); // format to 2 decimal places
                        calorie_Burned.setText(String.valueOf(Math.round(calories))); // round off to the nearest integer
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "There was a problem reading the data.", e);
                });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        readData(currentDateRange);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private void SetupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.stepTrack_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Step Tracker");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeViews() {

        step_counter = findViewById(R.id.step_counter);
        step_Distance = findViewById(R.id.step_Distance);
        calorie_Burned = findViewById(R.id.calorie_Burned);

        monday_steps = findViewById(R.id.monday_steps);
        monday_distance = findViewById(R.id.monday_distance);
        tuesday_steps = findViewById(R.id.tuesday_steps);
        tuesday_distance = findViewById(R.id.tuesday_distance);
        wednesday_steps = findViewById(R.id.wednesday_steps);
        wednesday_distance = findViewById(R.id.wednesday_distance);
        thursday_steps = findViewById(R.id.thursday_steps);
        thursday_distance = findViewById(R.id.thursday_distance);
        friday_steps = findViewById(R.id.friday_steps);
        friday_distance = findViewById(R.id.friday_distance);
        saturday_steps = findViewById(R.id.saturday_steps);
        saturday_distance = findViewById(R.id.saturday_distance);
        sunday_steps = findViewById(R.id.sunday_steps);
        sunday_distance = findViewById(R.id.sunday_distance);

        btnToday= findViewById(R.id.btnToday);
        btnWeek = findViewById(R.id.btnWeek);
        btnMonth = findViewById(R.id.btnMonth);

        SetupToolbar();
    }

}

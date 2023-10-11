package com.example.ilifestylepal;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class StepTrackerService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int stepCount;
    private SharedPreferences sharedPreferences;

    private int year, month, day, weekday;
    private int stepsToday = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Set SharedPreferences
        sharedPreferences = getSharedPreferences("StepTracker", Context.MODE_PRIVATE);

        //Set Sensor Manager and Listener
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //Check if the device has the necessary sensor for step tracker
        if (stepSensor != null) {
            // Step counter sensor is available
            GetCurrentDate();
            int currentStepCount = sharedPreferences.getInt("currentStep", 0);
            int previousStepCount = sharedPreferences.getInt("previousStep", 0);
            stepsToday = currentStepCount - previousStepCount;
            // Show the initial steps notification
            showStepsNotification(stepsToday);
            // Start the service in the foreground
            Notification notification = buildNotification("Tracking your steps... " + stepsToday);
            startForeground(1, notification);
        } else {
            // Step counter sensor is not available
            Notification notification = buildNotification("Unable to track steps. Step tracking may not be supported on this device.");
            startForeground(1, notification);
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // Unregister the listener for the step sensor
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    private Notification buildNotification(String s) {
        // Create a notification channel for Android 8.0 (API level 26) and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("StepTrackerChannel", "Step Tracker", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Define the target activity you want to open
        Intent targetIntent = new Intent(getApplicationContext(), StepTracker.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "StepTrackerChannel")
                .setSmallIcon(R.drawable.ic_step_tracker)
                .setContentTitle("Step Tracker")
                .setContentText(s)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, builder.build());

        return builder.build();
    }

    private void showStepsNotification(int stepsToday) {
        // Define the target activity you want to open
        Intent targetIntent = new Intent(getApplicationContext(), StepTracker.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the initial notification
        String notificationText = "Tracking your steps... " + stepsToday;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "StepTrackerChannel")
                .setSmallIcon(R.drawable.ic_step_tracker)
                .setContentTitle("Step Tracker")
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            GetCurrentDate();
            // Get the total step count
            stepCount = (int) sensorEvent.values[0];
            // Get the saved step count and date from SharedPreferences or Firebase
            int currentStepCount = sharedPreferences.getInt("currentStep", 0);
            int previousStepCount = sharedPreferences.getInt("previousStep", 0);
            int savedDay = sharedPreferences.getInt("stepDay", 1);
            int savedMonth = sharedPreferences.getInt("stepMonth", 1);
            int savedYear = sharedPreferences.getInt("stepYear", 2023);
            int savedGoal = sharedPreferences.getInt("stepGoal", 7000);
            // If the saved date is not today, reset the saved step count to zero
            if (!(savedDay == day && savedMonth == month && savedYear == year)) {
                previousStepCount = stepCount;
            }
            // Calculate the steps taken today and update the UI
            stepsToday = stepCount - previousStepCount;
            //If device is rebooted or the sensor was reset
            if (stepCount < currentStepCount) {
                stepsToday = stepCount + currentStepCount - previousStepCount;
                previousStepCount = stepCount - stepsToday;
            }
            // Save the current step count and date to SharedPreferences or Firebase
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("currentStep", stepCount);
            editor.putInt("previousStep", previousStepCount);
            editor.putInt("stepDay", day);
            editor.putInt("stepMonth", month);
            editor.putInt("stepYear", year);
            editor.apply();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String currentUserID = mAuth.getCurrentUser().getUid();
            DatabaseReference StepTrackerRef = FirebaseDatabase.getInstance().getReference().child("Step Tracker");

            String randomKey = currentUserID + month + day + year;

            HashMap stepMap = new HashMap();
            stepMap.put("step_id", randomKey);
            stepMap.put("step_uid", currentUserID);
            stepMap.put("step_timestamp", ServerValue.TIMESTAMP);
            stepMap.put("step_count", stepsToday);
            stepMap.put("step_weekday", getWeekday(weekday));
            stepMap.put("step_day", day);
            stepMap.put("step_month", getMonth(month));
            stepMap.put("step_year", year);
            stepMap.put("step_goal", savedGoal);

            StepTrackerRef.child(randomKey).updateChildren(stepMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.e("Step Tracker", "Step Taken Successfully");
                        showStepsNotification(stepsToday);
                    } else {
                        Log.e("Step Tracker", "Error: " + task.getException().getMessage());
                    }
                }
            });

            Log.e("Step1", currentStepCount + " ");
            Log.e("Step1", previousStepCount + " ");
            Log.e("Step1", stepsToday + " ");
            Log.e("Step1", savedDay + " ");
            Log.e("Step1", savedMonth + " ");
            Log.e("Step1", savedYear + " ");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Do nothing
    }

    private void GetCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1; // Month starts from 0, so add 1 to get the actual month
        day = calendar.get(Calendar.DAY_OF_MONTH);
        weekday = calendar.get(Calendar.DAY_OF_WEEK);
    }

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

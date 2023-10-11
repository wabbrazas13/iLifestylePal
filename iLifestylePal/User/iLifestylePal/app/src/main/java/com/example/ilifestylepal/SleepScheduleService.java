package com.example.ilifestylepal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
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
import java.util.concurrent.TimeUnit;

public class SleepScheduleService extends Service {

    private CountDownTimer timer;
    private int year, month, day, weekday;
    private String dateToday = "00000000";
    private NotificationCompat.Builder builder;
    private String action;
    private long startTimeMillis = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH) + 1; // Month starts from 0, so add 1 to get the actual month
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        dateToday = m + "" + d + "" + y;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Cancel previous notification
        SleepingTimeAlarmReceiver.cancelNotification(getApplicationContext());
        WakingTimeAlarmReceiver.cancelNotification(getApplicationContext());

        action = intent.getAction();

        if (action != null ) {
            if (action.equals("START")) {
                if (startTimeMillis == 0) {
                    startTimeMillis = System.currentTimeMillis();
                    startTimer();
                }
            } else {
                // Stop the timer
                if (timer != null) {
                    timer.cancel();
                }
                // Cancel the notification
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.cancel(5);
                // Stop the service
                stopSelf();
            }
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void BuildNotification() {
        // Define the target activity you want to open
        Intent targetIntent = new Intent(getApplicationContext(), SleepSchedule.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        builder = new NotificationCompat.Builder(getApplicationContext(), "SleepScheduleChannel")
                .setSmallIcon(R.drawable.ic_sleep_schedule)
                .setContentTitle("Sleep Schedule")
                .setContentText("Sleep Duration: 0 hours 0 minutes")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(5, builder.build());
    }

    private void startTimer() {
        //Build Notification
        BuildNotification();

        // Create a new CountDownTimer with 1-minute interval
        timer = new CountDownTimer(Long.MAX_VALUE, 60 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Calculate the Sleep Duration
                long sleepDuration = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startTimeMillis);

                // Calculate the elapsed hours and remaining minutes
                long hours = sleepDuration / 60;
                long remainingMinutes = sleepDuration % 60;

                // Build the notification message
                String notificationMessage = String.format("Sleep Duration : %d hours %d minutes", hours, remainingMinutes);

                // Update the content text of the existing notification
                builder.setContentText(notificationMessage);

                // Notify the notification manager with the updated notification
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(5, builder.build());

                StoreDataToFirebaseDatabase(sleepDuration);
            }

            @Override
            public void onFinish() {
                //Do nothing since it won't be triggered
            }
        };

        // Start the timer
        timer.start();
    }

    private void StoreDataToFirebaseDatabase(long sleepDuration) {
        //Firebase Instance
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseReference SleepScheduleRef = FirebaseDatabase.getInstance().getReference().child("Sleep Schedule");

        //Get Current Date
        GetCurrentDate();
        //Generate Random Key
        String randomKey = currentUserID + dateToday;
        long currentTimestamp = System.currentTimeMillis();

        SleepScheduleRef.child(randomKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    SleepScheduleRef.child(randomKey).child("sleep_duration").setValue(sleepDuration);
                    SleepScheduleRef.child(randomKey).child("end_timestamp").setValue(currentTimestamp);

                    if (sleepDuration == 0) {
                        SleepScheduleRef.child(randomKey).child("start_timestamp").setValue(currentTimestamp);
                    }
                } else {
                    HashMap schedMap = new HashMap();
                    schedMap.put("sleep_id", randomKey);
                    schedMap.put("sleep_uid", currentUserID);
                    schedMap.put("sleep_year", year);
                    schedMap.put("sleep_month", getMonth(month));
                    schedMap.put("sleep_day", day);
                    schedMap.put("sleep_duration", sleepDuration);
                    schedMap.put("sleep_weekday", getWeekday(weekday));
                    schedMap.put("start_timestamp", ServerValue.TIMESTAMP);
                    schedMap.put("end_timestamp", ServerValue.TIMESTAMP);

                    SleepScheduleRef.child(randomKey).updateChildren(schedMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Log.e("Sleep Schedule", "Sleep Schedule Added Successfully");
                            } else {
                                Log.e("Sleep Schedule", "Error: " + task.getException().getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Sleep Schedule", error.getMessage());
            }
        });
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

package com.example.ilifestylepal;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class BedtimeNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
        boolean isReminderNotificationOn = sharedPref.getBoolean("reminder_status", true);
        int reminder_minutes = sharedPref.getInt("reminder_minutes", 0);
        String ct = "It is time to sleep. You have " + reminder_minutes + " minutes more before bedtime.";

        Intent i = new Intent(context,SleepScheduleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "bedtimeNotification")
                .setSmallIcon(R.drawable.ic_clock)
                .setContentTitle("Sleep Schedule")
                .setContentText(ct)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        if (isReminderNotificationOn == true)   {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(123, builder.build());
        }
    }
}

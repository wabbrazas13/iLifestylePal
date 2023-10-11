package com.example.ilifestylepal;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class SleepingTimeAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Pending Intent For SleepSchedule.class
        Intent targetIntent = new Intent(context, SleepSchedule.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Inflate the custom notification layout
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_sleep_schedule);

        // Create a new intent to perform the action when the button is clicked
        Intent serviceIntent = new Intent(context, SleepScheduleService.class);
        // Add extra data to the intent
        serviceIntent.setAction("START");
        // Create the pending Intent
        PendingIntent servicePendingIntent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Set the click listener for the button
        contentView.setOnClickPendingIntent(R.id.notification_button, servicePendingIntent);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "SleepScheduleChannel")
                .setSmallIcon(R.drawable.ic_sleep_schedule)
                .setContentIntent(pendingIntent)
                .setCustomContentView(contentView)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());
    }

    public static void cancelNotification(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(200);
    }
}
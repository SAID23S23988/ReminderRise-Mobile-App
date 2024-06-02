package com.example.myapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String eventName = intent.getStringExtra("eventName");
        String eventDate = intent.getStringExtra("eventDate");

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "event_reminder_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Event Reminders", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Create intents for the action buttons
        Intent stopIntent = new Intent(context, StopNotificationReceiver.class);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent remindLaterIntent = new Intent(context, RemindLaterReceiver.class);
        remindLaterIntent.putExtra("eventName", eventName);
        remindLaterIntent.putExtra("eventDate", eventDate);
        PendingIntent remindLaterPendingIntent = PendingIntent.getBroadcast(context, 1, remindLaterIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.llogo) 
                .setContentTitle("Event Reminder")
                .setContentText("Event: " + eventName + " on " + eventDate)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.blue))
                .addAction(0, "Stop", stopPendingIntent)
                .addAction(0, "Remind Later", remindLaterPendingIntent);

        notificationManager.notify(1, builder.build());
    }

    public static class StopNotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
        }
    }

    public static class RemindLaterReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Reminder set for later", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                Intent reminderIntent = new Intent(context, NotificationReceiver.class);
                reminderIntent.putExtra("eventName", intent.getStringExtra("eventName"));
                reminderIntent.putExtra("eventDate", intent.getStringExtra("eventDate"));

                context.sendBroadcast(reminderIntent);
            }, 5 * 60 * 1000);
        }
    }
}


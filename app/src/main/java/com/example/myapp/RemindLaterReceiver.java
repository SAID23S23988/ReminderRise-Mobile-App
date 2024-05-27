package com.example.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public class RemindLaterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Reminder set for later", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> {
            Intent reminderIntent = new Intent(context, EventReminderReceiver.class);
            reminderIntent.putExtra("eventName", intent.getStringExtra("eventName"));
            reminderIntent.putExtra("eventDate", intent.getStringExtra("eventDate"));

            context.sendBroadcast(reminderIntent);
        }, 5 * 60 * 1000);
    }
}


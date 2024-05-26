package com.example.myapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class AddEventsActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    private DatabaseHelper databaseHelper;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText eventNameEditText = findViewById(R.id.eventNameEditText);
        DatePicker eventDatePicker = findViewById(R.id.eventDatePicker);
        Button saveButton = findViewById(R.id.saveButton);

        databaseHelper = new DatabaseHelper(this);
        currentUserId = getCurrentUserId();

        saveButton.setOnClickListener(view -> {
            String eventName = eventNameEditText.getText().toString();
            String eventDate = formatDate(eventDatePicker);

            if (eventName.trim().isEmpty()) {
                Toast.makeText(this, "Please enter event name", Toast.LENGTH_SHORT).show();
            } else {
                long eventId = databaseHelper.addEvent(eventName, eventDate, currentUserId); // Add event to database and get its unique ID
                if (eventId != -1) {
                    Toast.makeText(this, "Event saved: " + eventName + " on " + eventDate, Toast.LENGTH_SHORT).show();
                    eventNameEditText.setText("");

                    // Check and request notification permission
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
                    } else {
                        scheduleNotification(eventName, eventDate);
                    }
                } else {
                    Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Context context = this;
        final int id = item.getItemId();

        if (id == R.id.action_events) {
            Intent addEventIntent = new Intent(context, AddEventsActivity.class);
            startActivity(addEventIntent);
            return true;
        } else if (id == R.id.action_home) {
            Intent viewEventsIntent = new Intent(context, MainActivity.class);
            startActivity(viewEventsIntent);
            return true;
        } else if (id == R.id.action_view_events) {
            Intent viewEventsIntent = new Intent(context, ViewEventsActivity.class);
            startActivity(viewEventsIntent);
            return true;
        } else if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(context, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private String formatDate(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // Months are zero-based
        int year = datePicker.getYear();
        return day + "/" + month + "/" + year;
    }

    private int getCurrentUserId() {
        return 1;
    }

    private void scheduleNotification(String eventName, String eventDate) {
        new Handler().postDelayed(() -> sendNotification(eventName, eventDate), 5000); // 5000 milliseconds = 5 seconds
    }

    private void sendNotification(String eventName, String eventDate) {
        Intent stopIntent = new Intent(this, StopNotificationReceiver.class);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent remindLaterIntent = new Intent(this, RemindLaterReceiver.class);
        remindLaterIntent.putExtra("eventName", eventName);
        remindLaterIntent.putExtra("eventDate", eventDate);
        PendingIntent remindLaterPendingIntent = PendingIntent.getBroadcast(this, 0, remindLaterIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "event_reminder_channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Event Reminders", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.llogo)
                .setContentTitle("Event Reminder")
                .setContentText("Event: " + eventName + " on " + eventDate)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(new NotificationCompat.Action.Builder(0, "Stop", stopPendingIntent).build())
                .addAction(new NotificationCompat.Action.Builder(0, "Remind Later", remindLaterPendingIntent).build())
                .setColor(ContextCompat.getColor(this, R.color.blue));

        notificationManager.notify(1, builder.build());

        // Show a Toast message in the center of the screen
        Toast toast = Toast.makeText(this, "Event: " + eventName + " on " + eventDate, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}







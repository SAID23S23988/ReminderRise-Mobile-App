package com.example.myapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
                long eventId = databaseHelper.addEvent(eventName, eventDate, currentUserId);
                if (eventId != -1) {
                    Toast.makeText(this, "Event saved: " + eventName + " on " + eventDate, Toast.LENGTH_SHORT).show();
                    eventNameEditText.setText("");
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private int getCurrentUserId() {
        return 1; // افتراضياً يمكنك تعديلها لتتوافق مع متطلباتك
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(String eventName, String eventDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date eventDateObj;
        try {
            eventDateObj = dateFormat.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        long eventTimeInMillis = eventDateObj.getTime();
        long currentTimeInMillis = System.currentTimeMillis();
        long delay = eventTimeInMillis - currentTimeInMillis;

        if (delay > 0) {
            Intent notificationIntent = new Intent(this, NotificationReceiver.class);
            notificationIntent.putExtra("eventName", eventName);
            notificationIntent.putExtra("eventDate", eventDate);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, eventTimeInMillis, pendingIntent);
            }

            Toast.makeText(this, "Notification scheduled for event: " + eventName + " on " + eventDate, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Event date is in the past", Toast.LENGTH_SHORT).show();
        }
    }
}

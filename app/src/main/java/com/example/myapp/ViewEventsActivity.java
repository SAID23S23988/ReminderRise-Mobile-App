package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewEventsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);

        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadEvents();
    }

    private void loadEvents() {
        Cursor cursor = databaseHelper.getAllEvents(null); // Retrieve all events
        List<Event> events = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.getColumnEventId());
                int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_NAME);
                int dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_DATE);
                int userIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_USER_ID);

                if (idIndex != -1 && nameIndex != -1 && dateIndex != -1 && userIdIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String name = cursor.getString(nameIndex);
                    String date = cursor.getString(dateIndex);
                    long userId = cursor.getLong(userIdIndex);
                    events.add(new Event(id, name, date, userId));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        eventsAdapter = new EventsAdapter(events, this);
        eventsRecyclerView.setAdapter(eventsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
}









package com.example.myapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 2;

    private ImageView headerImageView;
    private EditText userNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addEventButton = findViewById(R.id.addEventButton);
        Button settingsButton = findViewById(R.id.settingsButton);
        ImageView viewEventsButton = findViewById(R.id.viewEventsButton);


        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddEventPage(v);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsPage(v);
            }
        });

        viewEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openViewEventsPage(v);
            }
        });
    }

    public void openAddEventPage(View view) {
        Intent intent = new Intent(this, AddEventsActivity.class);
        startActivity(intent);
    }

    public void openSettingsPage(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openViewEventsPage(View view) {
        Intent intent = new Intent(this, ViewEventsActivity.class);
        startActivity(intent);
    }
}




























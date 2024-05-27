package com.example.myapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.mindrot.jbcrypt.BCrypt;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "myapp.db";
    private static final int DATABASE_VERSION = 1;

    // User table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";

    private static final String TABLE_EVENTS = "events";
    private static final String COLUMN_EVENT_ID = "id";
    public static final String COLUMN_EVENT_NAME = "name";
    public static final String COLUMN_EVENT_DATE = "date";
    public static final String COLUMN_EVENT_USER_ID = "user_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_USER_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EVENT_NAME + " TEXT,"
                + COLUMN_EVENT_DATE + " TEXT,"
                + COLUMN_EVENT_USER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_EVENT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, hashPassword(password));
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public long addEvent(String name, String date, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_NAME, name);
        values.put(COLUMN_EVENT_DATE, date);
        values.put(COLUMN_EVENT_USER_ID, userId);
        long result = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return result;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USER_EMAIL + "=?", new String[]{email}, null, null, null);
        int passwordColumnIndex = cursor.getColumnIndex(COLUMN_USER_PASSWORD);
        boolean isValid = false;
        if (cursor.moveToFirst() && passwordColumnIndex != -1) {
            String hashedPassword = cursor.getString(passwordColumnIndex);
            isValid = BCrypt.checkpw(password, hashedPassword);
        }
        cursor.close();
        db.close();
        return isValid;
    }

    public Cursor getAllEvents(Long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTS;
        String[] selectionArgs = null;

        if (userId != null) {
            query += " WHERE " + COLUMN_EVENT_USER_ID + "=?";
            selectionArgs = new String[]{String.valueOf(userId)};
        }

        Cursor cursor = db.rawQuery(query, selectionArgs);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    public Cursor getEventsByUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_EVENT_USER_ID + "=?";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }



    public Cursor getEventById(long eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_EVENT_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(eventId)});
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public boolean deleteEvent(long eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EVENTS, COLUMN_EVENT_ID + " = ?", new String[]{String.valueOf(eventId)}) > 0;
    }
    public static String getColumnEventId() {
        return COLUMN_EVENT_ID;
    }
}







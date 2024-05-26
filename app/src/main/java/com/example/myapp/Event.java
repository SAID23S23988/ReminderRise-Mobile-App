// Event.java
package com.example.myapp;

public class Event {

    private long id;
    private String name;
    private String date;
    private long userId;

    public Event(long id, String name, String date, long userId) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public long getUserId() {
        return userId;
    }
}








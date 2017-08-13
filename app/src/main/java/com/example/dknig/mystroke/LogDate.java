package com.example.dknig.mystroke;

/**
 * Created by dknig on 5/10/2016.
 */

/* for notification use */
/* Initialize the required details of log() for pop-notification use (MainActivity Helper Class) */

public class LogDate {


    private String id;
    private String name;
    private String dateTime;

    // Constructor with parameter: (name of selected mood, time) from notification
    public LogDate(String name, String dateTime){
        this.name = name;
        this.dateTime = dateTime;
    }

    // Get and set method goes here
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

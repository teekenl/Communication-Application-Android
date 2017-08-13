package com.example.dknig.mystroke;

/**
 * Created by dknig on 28/09/2016.
 */


/* For mood Database use */
/* Initialize the required details of mood  (Helper Class for Mood Tab)*/

public class Mood {

    private String id;
    private String moodName;
    private String moodTime;

    // Empty constructor
    public Mood() {

    }

    // Constructor with parameter : (name of mood, time)
    public Mood(String moodName, String moodTime) {
        this.moodName = moodName;
        this.moodTime = moodTime;

    }

    // Get and set method goes here
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoodName() {
        return moodName;
    }

    public void setMoodName(String moodName) {
        this.moodName = moodName;
    }

    public String getMoodTime() {
        return moodTime;
    }

    public void setMoodTime(String moodTime) {
        this.moodTime = moodTime;
    }
}

package com.example.dknig.mystroke;

/**
 * Created by dknig on 20/08/2016.
 */

/* For event Database use */
/* Initialize the required details of event (EventTab Helper Class) */
public class Event {


    private String id;
    private String eventName;
    private String eventPlace;
    private String dateTime;
    private String time;
    private String eventRepeat;
    private String eventStaff;
    private String completed;
    private byte[] eventImage;
    private String notes;


    // Empty constructor
    public Event() {

    }

    // Get and set method at here
    public Event(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventRepeat() {
        return eventRepeat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setEventRepeat(String eventRepeat) {
        this.eventRepeat = eventRepeat;
    }

    public String getEventStaff() {
        return eventStaff;
    }

    public void setEventStaff(String eventStaff) {
        this.eventStaff = eventStaff;
    }

    public String getEventPlace() {
        return eventPlace;
    }

    public void setEventPlace(String eventPlace) {
        this.eventPlace = eventPlace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateTime() { return dateTime; }

    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public byte[] getEventImage() {
        return eventImage;
    }

    public void setEventImage(byte[] eventImage) {
        this.eventImage = eventImage;
    }
}

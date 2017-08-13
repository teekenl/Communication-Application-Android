package com.example.dknig.mystroke;

/**
 * Created by dknig on 1/10/2016.
 */

/* Initialize the required details for listing event (Event Tab Helper Class) */

public class ListItemEvent {
    private String eventName;
    private String _id;

    // Empty constructor
    public ListItemEvent() {

    }

    // get and set method goes here
    public ListItemEvent(String eventName) {
        this.eventName = eventName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}

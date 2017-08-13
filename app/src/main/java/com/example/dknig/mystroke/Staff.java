package com.example.dknig.mystroke;

import android.graphics.Bitmap;

/**
 * Created by dknig on 25/09/2016.
 */

/* for Staff Database use */
/* Initialize the required details of staff (StaffTab Helper Class) */
public class Staff {


    private String id;
    private String title;
    private String firstName;
    private String lastName;
    private byte[] bitmap;

    // Empty constructor
    public Staff() {

    }

    // Constructor with the parameter of role of staff, first name of staff and last name
    public Staff(String title, String firstName, String lastName) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Get and set method at here
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }
}

package com.example.dknig.mystroke;

import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "eventDB.db";
    public static final String TABLE_PRODUCTS = "eventlist";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EVENTNAME = "eventname";
    public static final String COLUMN_EVENTPLACE = "eventplace";
    public static final String COLUMN_DATETIME = "datetime";
    public static final String COLUMN_REPEAT = "eventrepeat";
    public static final String COLUMN_COMPLETE = "completed";
    public static final String COLUMN_STAFF = "eventstaff";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_TIME= "_time";
    public static final String COLUMN_IMAGE = "image";

    //We need to pass database information along to superclass
    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EVENTNAME + " TEXT, " +
                COLUMN_EVENTPLACE + " TEXT, " +
                COLUMN_DATETIME +  " TEXT, " +
                COLUMN_REPEAT + " TEXT, " +
                COLUMN_COMPLETE + " TEXT, " +
                COLUMN_STAFF + " TEXT, " +
                COLUMN_TIME + " TIME, " +
                COLUMN_NOTES + " TEXT, " +
                COLUMN_IMAGE  + " BLOB" +
                ");";
        db.execSQL(query);
    }

    //Lesson 51
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    //Add a new row to the database
    public void addEvent(Event events){
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENTNAME, events.getEventName());
        values.put(COLUMN_DATETIME, events.getDateTime());
        values.put(COLUMN_EVENTPLACE, events.getEventPlace());
        values.put(COLUMN_REPEAT, events.getEventRepeat());
        values.put(COLUMN_STAFF, events.getEventStaff());
        values.put(COLUMN_NOTES, events.getNotes());
        values.put(COLUMN_TIME, events.getTime());
        values.put(COLUMN_COMPLETE,events.getCompleted());
        values.put(COLUMN_IMAGE, events.getEventImage());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    //Delete a product from the database
    public void deleteEvent(Event events){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COLUMN_ID + "='" + events.getId() + "'", null);
        db.close();
    }

    // Updated Event
    public void updateEvent (Event events) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENTNAME, events.getEventName());
        values.put(COLUMN_DATETIME, events.getDateTime());
        values.put(COLUMN_EVENTPLACE, events.getEventPlace());
        values.put(COLUMN_REPEAT, events.getEventRepeat());
        values.put(COLUMN_STAFF, events.getEventStaff());
        values.put(COLUMN_TIME, events.getTime());
        values.put(COLUMN_IMAGE, events.getEventImage());
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_PRODUCTS, values, COLUMN_ID + " ='" + events.getId() + "'", null);
		db.close();
    }

    // Updated notes of event
    public void updateNotes (Event events) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTES, events.getNotes());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_PRODUCTS, values, COLUMN_ID + "='" + events.getId() + "'", null);
        db.close();
    }

    // Pick the event that meets the selected date from calendar view
    public String selectedDateDatabaseToString(String selectedDate) {
        String dbString ="";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_DATETIME + "='" + selectedDate +"';";
        Cursor recordSet = db.rawQuery(query,null);
        recordSet.moveToFirst();

        while(!recordSet.isAfterLast()) {
            if(recordSet.getString(recordSet.getColumnIndex("_id")) !=null) {
                    dbString += recordSet.getString(recordSet.getColumnIndex("eventname"));
                    dbString += ",";
            }
            recordSet.moveToNext();
        }

        db.close();
        return dbString;
    }

    // this is goint in record_TextView in the Main activity.
    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";// why not leave out the WHERE  clause?

        //Cursor points to a location in your results
        Cursor recordSet = db.rawQuery(query, null);
        //Move to the first row in your results
        recordSet.moveToFirst();

        //Position after the last row means the end of the results
        while (!recordSet.isAfterLast()) {
            // null could happen if we used our empty constructor
            if (recordSet.getString(recordSet.getColumnIndex("eventname")) != null) {
                dbString += recordSet.getString(recordSet.getColumnIndex("eventname"));
                dbString += ",";
            }
            recordSet.moveToNext();
        }
        db.close();
        return dbString;
    }

    // Pick up the event that are completed
    public String selectedeventCompleted(String eventDate) {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_DATETIME + "='" + eventDate + "';";// why not leave out the WHERE  clause?
        Cursor recordSet = db.rawQuery(query,null);
        recordSet.moveToFirst();

        while(!recordSet.isAfterLast()) {
            if(recordSet.getString(recordSet.getColumnIndex("eventname"))!=null) {
                dbString += recordSet.getString(recordSet.getColumnIndex("completed"));
                dbString += ",";
            }
            recordSet.moveToNext();
        }

        db.close();
        return dbString;
    }


    // select all the event that are already completed
    public String eventCompleted() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";// why not leave out the WHERE  clause?
        Cursor recordSet = db.rawQuery(query,null);
        recordSet.moveToFirst();

        while(!recordSet.isAfterLast()) {
            if(recordSet.getString(recordSet.getColumnIndex("eventname"))!=null) {
                dbString += recordSet.getString(recordSet.getColumnIndex("completed"));
                dbString += ",";
            }
            recordSet.moveToNext();
        }

        db.close();
        return dbString;
    }

    // Pick up the details of selected event
    public Event selectedEventList (String eventName) {
        SQLiteDatabase db = getWritableDatabase();
        String selectedEvent = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_EVENTNAME + " ='" + eventName + "';" ;
        Event event1 = new Event();
        Cursor recordSet = db.rawQuery(selectedEvent, null);

        recordSet.moveToFirst();
        while(!recordSet.isAfterLast()) {
            if(recordSet.getString(recordSet.getColumnIndex("eventname")) != null) {
                event1.setId(recordSet.getString(recordSet.getColumnIndex("_id")));
                event1.setEventName(recordSet.getString(recordSet.getColumnIndex("eventname")));
                event1.setEventPlace(recordSet.getString(recordSet.getColumnIndex("eventplace")));
                event1.setEventRepeat(recordSet.getString(recordSet.getColumnIndex("eventrepeat")));
                event1.setDateTime(recordSet.getString(recordSet.getColumnIndex("datetime")));
                event1.setEventStaff(recordSet.getString(recordSet.getColumnIndex("eventstaff")));
                event1.setCompleted(recordSet.getString(recordSet.getColumnIndex("completed")));
                event1.setNotes(recordSet.getString(recordSet.getColumnIndex("notes")));
                event1.setTime(recordSet.getString(recordSet.getColumnIndex("_time")));
                event1.setEventImage(recordSet.getBlob(recordSet.getColumnIndex("image")));
                break;
            }
        }
        db.close();
        return event1;
    }

    // Pick up the selected event by their event ID
    public Event selectedEventList2 (String eventID) {
        SQLiteDatabase db = getWritableDatabase();
        String selectedEvent = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_ID + " ='" + eventID + "';" ;
        Event event1 = new Event();
        Cursor recordSet = db.rawQuery(selectedEvent, null);

        recordSet.moveToFirst();
        while(!recordSet.isAfterLast()) {
            if(recordSet.getString(recordSet.getColumnIndex("eventname")) != null) {
                event1.setId(recordSet.getString(recordSet.getColumnIndex("_id")));
                event1.setEventName(recordSet.getString(recordSet.getColumnIndex("eventname")));
                event1.setEventPlace(recordSet.getString(recordSet.getColumnIndex("eventplace")));
                event1.setEventRepeat(recordSet.getString(recordSet.getColumnIndex("eventrepeat")));
                event1.setDateTime(recordSet.getString(recordSet.getColumnIndex("datetime")));
                event1.setEventStaff(recordSet.getString(recordSet.getColumnIndex("eventstaff")));
                event1.setCompleted(recordSet.getString(recordSet.getColumnIndex("completed")));
                event1.setTime(recordSet.getString(recordSet.getColumnIndex("_time")));
                event1.setNotes(recordSet.getString(recordSet.getColumnIndex("notes")));
                event1.setEventImage(recordSet.getBlob(recordSet.getColumnIndex("image")));
                break;
            }
        }
        db.close();
        return event1;
    }

    // select all the name of event in the list
    public String eventDateName () {
        String listName = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String name = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";

        Cursor recordSet = db.rawQuery(name, null);
        recordSet.moveToFirst();

        while(!recordSet.isAfterLast()) {
            if(recordSet.getString(recordSet.getColumnIndex("eventname")) != null) {
                listName+= recordSet.getString(recordSet.getColumnIndex("eventname"));
                listName+= ",";
            }
            recordSet.moveToNext();
        }
        db.close();
        return listName;
    }

    // select up all the date or time of event
    public String eventDateTime() {
        String listDate = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String date = "SELECT * FROM " + TABLE_PRODUCTS +  " WHERE 1";

        Cursor recordSet = db.rawQuery(date, null);
        recordSet.moveToFirst();

        while(!recordSet.isAfterLast()) {
            if(recordSet.getString(recordSet.getColumnIndex("datetime"))!=null) {
                listDate += recordSet.getString(recordSet.getColumnIndex("datetime"));
                listDate += ",";
            }
            recordSet.moveToNext();
        }

        db.close();
        return listDate;
    }

    // update the event when the selected tick box is ticked, otherwise false
    public void updateEventCompleted (String eventName, String completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLETE, completed);
        db.update(TABLE_PRODUCTS, values ,COLUMN_EVENTNAME + "='" + eventName + "'", null);
        db.close();;
    }

    // get all ID of event
    public String getEventID(String eventName) {
        String eventID ="";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_EVENTNAME + "='" +  eventName + "';";
        Cursor recordSet = db.rawQuery(query,null);
        recordSet.moveToFirst();

            while(!recordSet.isAfterLast()) {
                if(recordSet.getString(recordSet.getColumnIndex("_id"))!=null) {
                    eventID += recordSet.getString(recordSet.getColumnIndex("_id"));
                    break;
                }
            }

        db.close();
        return eventID;
    }

    // get and insert all the event into the list type variable for activity use
    public List<Event> allEventList() {
        List<Event> contactList = new ArrayList<Event>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event events = new Event();
                events.setId(cursor.getString(0));
                events.setTime(cursor.getString(8));
                events.setCompleted(cursor.getString(5));
                events.setEventImage(cursor.getBlob(9));
                events.setDateTime(cursor.getString(3));
                events.setEventPlace(cursor.getString(2));
                events.setEventRepeat(cursor.getString(4));
                events.setEventStaff(cursor.getString(6));
                events.setNotes(cursor.getString(7));
                events.setEventName(cursor.getString(1));

                // Adding contact to list
                contactList.add(events);
            } while (cursor.moveToNext());
        }
        // close inserting data from database
        db.close();
        // return contact list
        return contactList;
    }

    // return corresponding the event details by selected date
    public List<Event> allSelectedEventList(String selectedDate) {
        List<Event> contactList = new ArrayList<Event>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_DATETIME + "='" + selectedDate + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event events = new Event();
                events.setId(cursor.getString(0));
                events.setTime(cursor.getString(8));
                events.setCompleted(cursor.getString(5));
                events.setEventImage(cursor.getBlob(9));
                events.setDateTime(cursor.getString(3));
                events.setEventPlace(cursor.getString(2));
                events.setEventRepeat(cursor.getString(4));
                events.setEventStaff(cursor.getString(6));
                events.setNotes(cursor.getString(7));
                events.setEventName(cursor.getString(1));

                // Adding contact to list
                contactList.add(events);
            } while (cursor.moveToNext());
        }
        // close inserting data from database
        db.close();
        // return contact list
        return contactList;
    }


}
package com.example.dknig.mystroke;

/**
 * Created by dknig on 25/09/2016.
 */
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

import com.example.dknig.mystroke.Staff;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler2 extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "staffDB.db";
    private static final String TABLE_PRODUCTS = "stafflist";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_STAFFFIRST = "firstname";
    public static final String COLUMN_STAFFLAST = "lastname";
    public static final String COLUMN_BITMAP = "bitmap";

    //We need to pass database information along to superclass
    public MyDBHandler2(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_STAFFFIRST + " TEXT, " +
                COLUMN_STAFFLAST +  " TEXT, " +
                COLUMN_BITMAP + " BLOB " +
                ");";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    //Add a new row to the database
    public void addStaff(Staff staffs){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, staffs.getTitle());
        values.put(COLUMN_STAFFFIRST, staffs.getFirstName());
        values.put(COLUMN_STAFFLAST, staffs.getLastName());
        values.put(COLUMN_BITMAP, staffs.getBitmap());
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    // Delete the selected staff from the database
    public void deleteStaff (Staff staffID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS,  COLUMN_ID + "='" + staffID.getId() + "'", null);
        db.close();
    }

    // Delete all the staff from database  (WARNING not used this)
    public void deletedAllStaff() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE * FROM " +  TABLE_PRODUCTS + ";");
        db.close();
    }

    // Update the selected details of particular staff
    public void updateStaff (Staff staffs) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, staffs.getTitle());
        values.put(COLUMN_STAFFFIRST, staffs.getFirstName());
        values.put(COLUMN_STAFFLAST, staffs.getLastName());
        values.put(COLUMN_BITMAP, staffs.getBitmap());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_PRODUCTS, values, COLUMN_ID + "='" + staffs.getId() + "'", null);
        db.close();
    }

    // Get the all the details that corresponding to the staff
    public Staff selectedStaffList (String staffFirstName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectedStaff = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STAFFFIRST + " = '" + staffFirstName + "';";
        Staff staffs = new Staff();
        Cursor recordSet = db.rawQuery(selectedStaff, null);
        recordSet.moveToFirst();

        while(!recordSet.isAfterLast()) {
            if(recordSet.getString(recordSet.getColumnIndex("firstname")) != null) {
                staffs.setBitmap(recordSet.getBlob(4));
                staffs.setFirstName(recordSet.getString(recordSet.getColumnIndex("firstname")));
                staffs.setTitle(recordSet.getString(recordSet.getColumnIndex("title")));
                staffs.setLastName(recordSet.getString(recordSet.getColumnIndex("lastname")));
                staffs.setId(recordSet.getString(recordSet.getColumnIndex("_id")));
                break;
            }
        }
        db.close();
        return staffs;
    }

    // this is goint in record_TextView in the Main activity.
    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";// why not leave out the WHERE  clause?

        //Cursor points to a location in your results
        Cursor recordSet = db.rawQuery(query, null);
        //Move to the first row in your results
        recordSet.moveToFirst();

        //Position after the last row means the end of the results
        while (!recordSet.isAfterLast()) {
            // null could happen if we used our empty constructor
            if (recordSet.getString(recordSet.getColumnIndex("firstname")) != null) {
                dbString += recordSet.getString(recordSet.getColumnIndex("title"));
                dbString += ".";
                dbString += recordSet.getString(recordSet.getColumnIndex("firstname"));
                dbString += ",";
            }
            recordSet.moveToNext();
        }
        db.close();
        return dbString;
    }

    // Get the saved image that corresponding to the selected staff
    public byte[] databaseToString2(String id){
        byte[] dbString;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_ID  + "='"+ id +"';";// why not leave out the WHERE  clause?

        //Cursor points to a location in your results
        Cursor recordSet = db.rawQuery(query, null);
        //Move to the first row in your results
        recordSet.moveToFirst();

        db.close();
        return recordSet.getBlob(4);
    }

    // return the total number of staff
    public int numOfStaff() {
        String countQuery = "SELECT  * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Get and insert all staff details into list variable type
    public List<Staff> allStaffList() {
        List<Staff> contactList = new ArrayList<Staff>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Staff contact = new Staff();
                contact.setId(cursor.getString(0));
                contact.setFirstName(cursor.getString(2));
                contact.setLastName(cursor.getString(3));
                contact.setTitle(cursor.getString(1));
                contact.setBitmap(cursor.getBlob(4));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        // close inserting data from database
        db.close();
        // return contact list
        return contactList;
    }


}



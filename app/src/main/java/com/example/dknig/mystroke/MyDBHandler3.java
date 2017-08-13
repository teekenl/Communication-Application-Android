package com.example.dknig.mystroke;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

/**
 * Created by dknig on 28/09/2016.
 */
public class MyDBHandler3 extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "moodDB.db";
    private static final String TABLE_PRODUCTS = "moodlist";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MOODNAME = "moodtype";
    public static final String COLUMN_MOODATETIME = "moodtime";


    public MyDBHandler3(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    // Database created
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MOODNAME + " TEXT, " +
                COLUMN_MOODATETIME + " TEXT " +
                ");";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Update the mood feeling
    public void updateMood(String moodName, String datetime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MOODNAME,moodName);
        values.put(COLUMN_MOODATETIME, datetime);

        db.update(TABLE_PRODUCTS, values, COLUMN_MOODNAME + "='" + moodName + "'", null);
        db.close();

    }

    // Add a new mood feeling for graph use
    public void addMood(String moodName, String datetime) {
        SQLiteDatabase db = this .getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MOODNAME, moodName);
        values.put(COLUMN_MOODATETIME, datetime);

        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    // Select all the type of mood feeling from database
    public String databasetoString(){
        String dbString="";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";
        Cursor recordSet = db.rawQuery(query, null);
        recordSet.moveToFirst();

        while(!recordSet.isAfterLast()) {
            if(recordSet.getString(recordSet.getColumnIndex("moodtype"))!=null) {

                dbString += recordSet.getString(recordSet.getColumnIndex("moodtype"));
                dbString += ",";
            }
            recordSet.moveToNext();
        }

        db.close();
        return dbString;
    }

    // Select all the date or time of mood feeling from database
    public String databasetoString2(){
        String dbString="";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";
        Cursor recordSet = db.rawQuery(query, null);
        recordSet.moveToFirst();

        while(!recordSet.isAfterLast()) {
            if(recordSet.getString(recordSet.getColumnIndex("moodtime")) !=null) {
                dbString += recordSet.getString(recordSet.getColumnIndex("moodtime"));
                dbString += ",";
            }
            recordSet.moveToNext();
        }


        db.close();
        return dbString;
    }

}


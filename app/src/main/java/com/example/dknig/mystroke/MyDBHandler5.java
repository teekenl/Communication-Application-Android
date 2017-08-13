package com.example.dknig.mystroke;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

/**
 * Created by dknig on 5/10/2016.
 */
public class MyDBHandler5 extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "log.db";
    public static final String TABLE_PRODUCTS = "loglist";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME= "_name";
    public static final String COLUMN_TIME = "_time";


    public MyDBHandler5(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //Database created
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_TIME + " TEXT " +
                ");";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Add the new recorded log which generally includes the time of response, type of mood feeling for
    // (6 hours) pop-up notification use
    public void addLog(LogDate logs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME,logs.getName());
        values.put(COLUMN_TIME,logs.getDateTime());
        db.insert(TABLE_PRODUCTS,null,values);
        db.close();
    }

    // Get the last recorded log
    public String lastLog() {

        String lastdate = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";
        Cursor recordSet = db.rawQuery(query,null);
        recordSet.moveToFirst();
        while(!recordSet.isAfterLast()) {
            if(recordSet.getString(recordSet.getColumnIndex("_name"))!=null) {
                lastdate =  recordSet.getString(recordSet.getColumnIndex("_time"));
            }
            recordSet.moveToNext();
        }

        db.close();
        return lastdate;
    }

}

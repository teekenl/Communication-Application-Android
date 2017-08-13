package com.example.dknig.mystroke;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dknig on 1/10/2016.
 */
public class MyDBHandler4 extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "listitem.db";
    public static final String TABLE_PRODUCTS = "itemlist";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME= "_name";

    //We need to pass database information along to superclass
    public MyDBHandler4(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //Database created
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT " +
                ");";
        db.execSQL(query);
    }

    //Lesson 51
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Add a new item to the pre-built add event list
    public void addItemList(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, itemName);

        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    // select all the existing the pre-built event list that are added by patients or nurses.
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
                if (recordSet.getString(recordSet.getColumnIndex("_name")) != null) {
                    dbString += recordSet.getString(recordSet.getColumnIndex("_name"));
                    dbString += ",";
                }
                recordSet.moveToNext();
            }
            db.close();
            return dbString;
    }

}

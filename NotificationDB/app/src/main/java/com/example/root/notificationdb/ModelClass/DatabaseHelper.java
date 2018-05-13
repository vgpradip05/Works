package com.example.root.notificationdb.ModelClass;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "notif_toast_db";
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(App.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + App.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertData(String appName,String type,String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        values.put(App.APP_NAME, appName);
        values.put(App.INSTANCE_TYPE, type);
        values.put(App.DATA, data);
        long id = db.insert(App.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<App> getAllApps(String type) {
        List<App> apps = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + App.TABLE_NAME +" WHERE "+App.INSTANCE_TYPE + " = '"+ type + "' ORDER BY " + App.COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                App app = new App();
                app.setId(cursor.getInt(cursor.getColumnIndex(App.COLUMN_ID)));
                app.setAppName(cursor.getString(cursor.getColumnIndex(App.APP_NAME)));
                app.setLastTime(cursor.getString(cursor.getColumnIndex(App.COLUMN_TIMESTAMP)));
                app.setTypeOfInstance(cursor.getString(cursor.getColumnIndex(App.INSTANCE_TYPE)));
                app.setData(cursor.getString(cursor.getColumnIndex(App.DATA)));

                apps.add(app);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return apps;
    }
    public void deleteSingleItem(App app){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(App.TABLE_NAME, App.COLUMN_ID + " = ?",
                new String[]{String.valueOf(app.getId())});
        db.close();
    }
    public void deleteList(String type){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(App.TABLE_NAME, App.INSTANCE_TYPE + " = ?",
                new String[]{type});
        db.close();
    }
    public void deleteOlder(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int position = sharedPreferences.getInt(Constants.SPINNER_DATA, 0);
        double diff = 0.0;
        switch (position){
            case 0 :{
                return;
            }case 1:{
                diff = 0.5; // for 12 hours half day
                break;
            }case 2:{
                diff = 1.0; // for 24 hours one day
                break;
            }case 3:{
                diff = 2.0;// for 48 hours two days
                break;
            }case 4:{
                diff = 7.0;// for one week 7 days
                break;
            }
        }
        diff = 0 - diff*24.0*60.0*60.0*1000.0; //convert to negative milli seconds for subtraction 24 hours 60 minutes 60 seconds and 1000 milliseconds.
        SimpleDateFormat formatter1= new SimpleDateFormat(Constants.FORMATE_TIMSTAMP);
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MILLISECOND,(int)diff);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(App.TABLE_NAME, App.COLUMN_TIMESTAMP + " <= ? ",
                new String[]{formatter1.format(cal.getTime())});
        db.close();
    }
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ App.TABLE_NAME);
        db.close();
    }

}
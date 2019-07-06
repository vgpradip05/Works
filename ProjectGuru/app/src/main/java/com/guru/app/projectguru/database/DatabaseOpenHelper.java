package com.guru.app.projectguru.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.guru.app.projectguru.utils.Constants;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static DatabaseOpenHelper _instance;
    private static SQLiteDatabase _database;

    private DatabaseOpenHelper(Context context) {
        super(context, Constants.DatabaseHelper.DATABASE_NAME, null, Constants.DatabaseHelper.DATABASE_VERSION_1);

    }

    public static SQLiteDatabase getInstance(Context context) {
        if (_instance == null) {
            _instance = new DatabaseOpenHelper(context);
        }
        if (_database == null) {
            _database = _instance.getWritableDatabase();
        }
        return _database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }

        // Create User Table;
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + DatabaseTableHelper.User.TABLE_NAME + " ("
                + DatabaseTableHelper.User.USER_ID + " TEXT, "
                + DatabaseTableHelper.User.PASSWORD + " TEXT, "
                + DatabaseTableHelper.User.RGM + " TEXT, "
                + DatabaseTableHelper.User.TMID + " TEXT )");

        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTableHelper.OrderTypes.TABLE_NAME + " ("
                    + DatabaseTableHelper.OrderTypes.ORDER_TYPES + " TEXT,"
                    + DatabaseTableHelper.OrderTypes.ORDER_TYPE_CODE + " TEXT) "
            );
        } catch (Exception e) {}try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTableHelper.Master.TABLE_NAME + " ("
                    + DatabaseTableHelper.Master.CITIES+ " TEXT,"
                    + DatabaseTableHelper.Master.SECTORS_KEYS+ " TEXT,"
                    + DatabaseTableHelper.Master.SECTORS_VALUES+ " TEXT,"
                    + DatabaseTableHelper.Master.UNIVERCITIES+ " TEXT) "
            );
        } catch (Exception e) {}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == Constants.DatabaseHelper.DATABASE_VERSION_2) {
            runVersion2Script(db, 1);
        }
    }

    private void runVersion2Script(SQLiteDatabase db, int oldVersion) {

        try {
            String modifyCommodityMasterTable = "ALTER TABLE " + DatabaseTableHelper.OrderTypes.TABLE_NAME + " ADD COLUMN "
                    + DatabaseTableHelper.OrderTypes.ORDER_TYPE_CODE + " TEXT ";
            db.execSQL(modifyCommodityMasterTable);
        } catch (Exception e) {
        }

    }
}
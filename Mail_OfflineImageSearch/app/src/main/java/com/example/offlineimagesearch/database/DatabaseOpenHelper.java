/**
 *
 */
package com.example.offlineimagesearch.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static SQLiteDatabase _database;
    private static Context mContext;

    private DatabaseOpenHelper(Context context) {
        super(context, DatabaseConfig.DATABASE_NAME, null, DatabaseConfig.DATABASE_VERSION_1);
        mContext = context;
    }

    public static SQLiteDatabase getInstance(Context context) {
        if (_database == null) {
            DatabaseOpenHelper openHelper = new DatabaseOpenHelper(context);
            _database = openHelper.getWritableDatabase();
        }
        return _database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /**
         * Version 1
         */
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + DatabaseTableHelper.Photo.TABLE_NAME + " ( "
                    + DatabaseTableHelper.Photo.SEARCH_KEY + " TEXT,"
                    + DatabaseTableHelper.Photo.ID + " TEXT,"
                    + DatabaseTableHelper.Photo.OWNER + " TEXT,"
                    + DatabaseTableHelper.Photo.SECRET + " TEXT,"
                    + DatabaseTableHelper.Photo.SERVER + " TEXT,"
                    + DatabaseTableHelper.Photo.FARM + " TEXT,"
                    + DatabaseTableHelper.Photo.TITLE + " TEXT,"
                    + DatabaseTableHelper.Photo.ISPUBLIC + " TEXT,"
                    + DatabaseTableHelper.Photo.ISFRIEND + " TEXT,"
                    + DatabaseTableHelper.Photo.ISFAMILY + " TEXT"

                    + " )"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch (oldVersion) {
            case 1:

        }

    }
    public interface DatabaseConfig {

        String DATABASE_NAME = "Offlinedb.db3";
        // String DATABASE_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/offlineImageSearch/Offlinedb.db3";
        int DATABASE_VERSION_1 = 1;


    }
}

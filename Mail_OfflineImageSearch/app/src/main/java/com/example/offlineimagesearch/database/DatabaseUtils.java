package com.example.offlineimagesearch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.offlineimagesearch.models.Photo;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {

    private static boolean isExists(SQLiteDatabase database, String primaryKeyValue, String tableName,
                                    String primaryKey) {
        boolean isExists = false;
        Cursor cursor = database.query(tableName, null, primaryKey + "=?", new String[]{"" + primaryKeyValue}, null,
                null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExists = true;
            }
        }
        cursor.close();
        return isExists;
    }

    private static boolean isExists(SQLiteDatabase database, String table, String columnNames, String[] values) {
        boolean isExists = false;
        Cursor cursor = database.query(table, null, columnNames, values, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExists = true;
            }
        }
        cursor.close();
        return isExists;
    }

    public static void insertOrUpdatePhotos(Context context, List<Photo> photos,String searchKey) {
        SQLiteDatabase database = DatabaseOpenHelper.getInstance(context);
        for (Photo photo : photos) {
            photo.setSearchKey(searchKey);
            insertOrUpdatePhoto(database, photo);
        }
    }

    private static void insertOrUpdatePhoto(SQLiteDatabase database, Photo photo) {


        ContentValues values = new ContentValues();
        values.put(DatabaseTableHelper.Photo.SEARCH_KEY, photo.getSearchKey());
        values.put(DatabaseTableHelper.Photo.ID, photo.getId());
        values.put(DatabaseTableHelper.Photo.OWNER, photo.getOwner());
        values.put(DatabaseTableHelper.Photo.SECRET, photo.getSecret());
        values.put(DatabaseTableHelper.Photo.SERVER, photo.getServer());
        values.put(DatabaseTableHelper.Photo.FARM, photo.getFarm());
        values.put(DatabaseTableHelper.Photo.TITLE, photo.getTitle());
        values.put(DatabaseTableHelper.Photo.ISPUBLIC, photo.getIspublic());
        values.put(DatabaseTableHelper.Photo.ISFRIEND, photo.getIsfriend());
        values.put(DatabaseTableHelper.Photo.ISFAMILY, photo.getIsfamily());


        boolean isExists = isExists(database, DatabaseTableHelper.Photo.TABLE_NAME,
                DatabaseTableHelper.Photo.ID + "= ? AND "
                        + DatabaseTableHelper.Photo.SECRET + "=? AND "
                        + DatabaseTableHelper.Photo.SERVER + "=? ",
                new String[]{photo.getId(), photo.getSecret(), photo.getServer()});
        if (isExists) {
            database.update(DatabaseTableHelper.Photo.TABLE_NAME, values,
                    DatabaseTableHelper.Photo.ID + "= ? AND " +
                            DatabaseTableHelper.Photo.SECRET + "= ? AND "
                            + DatabaseTableHelper.Photo.SERVER + "=?",
                    new String[]{photo.getId(), photo.getSecret(), photo.getServer()});
        } else {
            database.insert(DatabaseTableHelper.Photo.TABLE_NAME, null, values);
        }
    }

    public static ArrayList<Photo> getPhotos(Context context, String searchKey) {
        SQLiteDatabase database = DatabaseOpenHelper.getInstance(context);
        ArrayList<Photo> photos = new ArrayList<>();
        String query = "SELECT * FROM " + DatabaseTableHelper.Photo.TABLE_NAME +
                " WHERE " + DatabaseTableHelper.Photo.SEARCH_KEY + " ='" + searchKey + "'";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Photo photo = getPhotoModel(cursor);
                photos.add(photo);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return photos;
    }

    public static String[] getDistinctSearchKeys(Context context){
        SQLiteDatabase database = DatabaseOpenHelper.getInstance(context);
        ArrayList<String> keys = new ArrayList<>();

        String query = "SELECT DISTINCT "+DatabaseTableHelper.Photo.SEARCH_KEY +
                " FROM "+DatabaseTableHelper.Photo.TABLE_NAME;

        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String key = cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Photo.SEARCH_KEY));
                keys.add(key);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return keys.toArray(new String[0]);
    }

    private static Photo getPhotoModel(Cursor cursor) {
        Photo photo = new Photo();
        try {
            photo.setId(cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Photo.ID)));
            photo.setSearchKey(cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Photo.SEARCH_KEY)));
            photo.setOwner(cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Photo.OWNER)));
            photo.setSecret(cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Photo.SECRET)));
            photo.setServer(cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Photo.SERVER)));
            photo.setFarm(cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Photo.FARM)));
            photo.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Photo.TITLE)));
            photo.setIspublic(cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Photo.ISPUBLIC)));
            photo.setIsfriend(cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Photo.ISFRIEND)));
            photo.setIsfamily(cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Photo.ISFAMILY)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photo;
    }



}

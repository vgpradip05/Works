package com.guru.app.projectguru.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.guru.app.projectguru.models.Master;
import com.guru.app.projectguru.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class DataBaseUtils {

    private static boolean isExists(SQLiteDatabase database, String primaryKeyValue, String tableName,
                                    String primaryKey) {
        boolean isExists = false;
        Cursor cursor;
        if(TextUtils.isEmpty(primaryKeyValue)){
            cursor = database.rawQuery("SELECT * from "+tableName,null);
        }else {
            cursor = database.query(tableName, null, primaryKey + "=?", new String[]{"" + primaryKeyValue}, null,
                    null, null);
        }
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExists = true;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return isExists;
    }

    public static void insertMasterData(Context context, Master master){
        SQLiteDatabase database = DatabaseOpenHelper.getInstance(context);
        database.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(DatabaseTableHelper.Master.CITIES,new JSONObject().put(Constants.MasterData.CITIES,new JSONArray(master.getCities())).toString());
            values.put(DatabaseTableHelper.Master.UNIVERCITIES,new JSONObject().put(Constants.MasterData.UNIVERSITIES,new JSONArray(master.getUniversities())).toString());
            values.put(DatabaseTableHelper.Master.SECTORS_KEYS,new JSONObject().put(Constants.MasterData.SECTORS,new JSONArray(master.getSectors().keySet())).toString());
            values.put(DatabaseTableHelper.Master.SECTORS_VALUES,new JSONObject().put(Constants.MasterData.SECTORS,new JSONArray(master.getSectors().values())).toString());

            if (!isExists(database, null, DatabaseTableHelper.Master.TABLE_NAME,
                    null)) {
                database.insert(DatabaseTableHelper.Master.TABLE_NAME, null, values);
            } else {
                database.delete(DatabaseTableHelper.Master.TABLE_NAME,null,null);
                database.insert(DatabaseTableHelper.Master.TABLE_NAME, null, values);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }
    public static Master getMasterData(Context context){
        SQLiteDatabase database = DatabaseOpenHelper.getInstance(context);
        String query = "Select * from " + DatabaseTableHelper.Master.TABLE_NAME ;
        Cursor cursor = database.rawQuery(query, null);
        String cities="",universities="",sectorKeys="",sectorValues="";
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                cities = cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Master.CITIES));
                universities = cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Master.UNIVERCITIES));
                sectorKeys = cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Master.SECTORS_KEYS));
                sectorValues = cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Master.SECTORS_VALUES));
            }
        }
        JSONObject citiesObj = new JSONObject();
        JSONObject univObj = new JSONObject();
        JSONObject sectorKeysObj = new JSONObject();
        JSONObject sectorvaluesObj = new JSONObject();
        try {
            citiesObj = new JSONObject(cities);
            univObj = new JSONObject(universities);
            sectorKeysObj = new JSONObject(sectorKeys);
            sectorvaluesObj = new JSONObject(sectorValues);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Master master = new Master();
        ArrayList<String> citiesList = new ArrayList<>();
        ArrayList<String> universityList = new ArrayList<>();
        HashMap<String,ArrayList<String>>sectorsList = new HashMap<>();
        try {
            for (int pos = 0; pos < citiesObj.optJSONArray(Constants.MasterData.CITIES).length(); pos++) {
                citiesList.add((String) citiesObj.optJSONArray(Constants.MasterData.CITIES).get(pos));
            }
            master.setCities(citiesList);
            for (int pos = 0; pos < univObj.optJSONArray(Constants.MasterData.UNIVERSITIES).length(); pos++) {
                universityList.add((String) univObj.optJSONArray(Constants.MasterData.UNIVERSITIES).get(pos));
            }
            master.setUniversities(universityList);
            for (int pos = 0; pos < sectorKeysObj.optJSONArray(Constants.MasterData.SECTORS).length(); pos++) {
                ArrayList<String> valueList = new ArrayList<>();
                for(int pos2 = 0 ; pos2<((JSONArray)sectorvaluesObj.optJSONArray(Constants.MasterData.SECTORS).get(pos)).length() ; pos2++){
                    valueList.add((String) ((JSONArray)sectorvaluesObj.optJSONArray(Constants.MasterData.SECTORS).get(pos)).get(pos2));
                }
                sectorsList.put((String) sectorKeysObj.optJSONArray(Constants.MasterData.SECTORS).get(pos),valueList);
            }
            master.setSectors(sectorsList);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        return master;
    }
   /* public static void insertUserDetails(Context context, ModelLoginResponse loginResponse) {
        SQLiteDatabase database = DatabaseOpenHelper.getInstance(context);
        database.beginTransaction();

        try {
            ModelUser userDetails = loginResponse.getUser();
            ContentValues values = new ContentValues();
            values.put(DatabaseTableHelper.User.USER_ID, userDetails.getUserId());
            values.put(DatabaseTableHelper.User.PASSWORD, userDetails.getPassword());
            values.put(DatabaseTableHelper.User.TMID, userDetails.getTmID());
            values.put(DatabaseTableHelper.User.RGM, userDetails.getRegMobileNum());
            if (!isExists(database, userDetails.getUserId(), DatabaseTableHelper.User.TABLE_NAME,
                    DatabaseTableHelper.User.USER_ID)) {
                database.insert(DatabaseTableHelper.User.TABLE_NAME, null, values);
            } else {
                database.update(DatabaseTableHelper.User.TABLE_NAME, values, DatabaseTableHelper.User.USER_ID + "=?",
                        new String[]{userDetails.getUserId()});
            }
        } catch (Exception e) {
            Logger.log(e);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private static String getParamValues(String serverOfferID,Context context) {
        String paramValue ="";
        SQLiteDatabase database = DatabaseOpenHelper.getInstance(context);
        String query = "Select * from " + DatabaseTableHelper.Offers.TABLE_NAME + " where " + DatabaseTableHelper.Offers.SERVEROFFERID  +" = '" + serverOfferID+"'";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                paramValue = cursor.getString(cursor.getColumnIndex(DatabaseTableHelper.Offers.PARAM_VALUES));
            }
        }
        cursor.close();
        return paramValue;
    }*/
}

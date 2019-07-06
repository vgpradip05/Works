package com.guru.app.projectguru.database;


import android.content.Context;
import android.content.SharedPreferences;

import com.guru.app.projectguru.utils.Constants;

public class LocalPreferences {
    private static Context context;

    public LocalPreferences(Context context){
        this.context = context;
    }

    private static SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.LocalPrefKeys.LOCAL_PREF, 0);
    private static SharedPreferences.Editor editor = sharedPreferences.edit();

    public static void setMasterDataFlag(Boolean flag){
        editor.putBoolean(Constants.LocalPrefKeys.MASTER_DATA_FLAG,flag);
        editor.commit();
    }
    public static boolean getMasterDataFlag(){
        return sharedPreferences.getBoolean(Constants.LocalPrefKeys.MASTER_DATA_FLAG,false);
    }

    public void setIsEmailSaved(Boolean flag){
        editor.putBoolean(Constants.LocalPrefKeys.IS_SAVED_EMAIL,flag);
        editor.commit();
    }
    public boolean getIsEmailSaved(){
        return sharedPreferences.getBoolean(Constants.LocalPrefKeys.IS_SAVED_EMAIL,false);
    }

}

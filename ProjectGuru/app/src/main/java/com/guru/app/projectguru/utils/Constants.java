package com.guru.app.projectguru.utils;

import android.os.Environment;

public class Constants {
    public interface Regex{
        String SIMPLE_TEXT = "^[\\p{L} .'-]+$";
    }
    public interface GoogleSignInKeys{
        String REQUEST_ID_TOKEN="867435280745-unla1clpfq0g59e8br4g6u3ptevee7lf.apps.googleusercontent.com";
    }
    public interface ErrorMessages{
        String EMPTY_NAME = "Please Enter Name";
        String EMPTY_EMAIL= "Please Enter Email";
        String EMPTY_PHONE = "Please Enter Mobile No";
        String EMPTY_FIELD = "This Field is Required";
        String INVALID_FIELD = "This Field is not valid";
        String INVALID_NAME = "Please enter valid name";
        String INVALID_EMAIL = "Please enter valid Email";
        String INVALID_PHONE = "Please enter valid Mobile No";
        String VALID_DATA = "Valid Data";
        String INVALID_PASSWORD = "Password Should be greater than 6 digits and less than 12!";
        String SELECT_PDF = "Please Select Pdf";
        String NO_FILE_CHOSEN = "No File chosen";

    }
    public interface MasterData{
        String SECTORS = "sectors";
        String CITIES = "cities";
        String UNIVERSITIES = "universities";
    }
    public interface StringArrays{
        String[] CITEIS={"Mumbai","Pune","Bangluru","Chennai"};
        String[] UNIVERSITIES={"State University","IIT","NIT","Foreign"};
        String[] PROFESSIONS ={"Software Technologies","Banking","Shares"};
    }
    public  interface FireBasePaths {
        String STORAGE_PATH_UPLOADS = "uploads/";
        String DATABASE_PATH_UPLOADS = "uploads";
        String DATABASE_PATH_MASTER = "master";
        String DATABASE_PATH_UNIV_TREE = "univtree";
        String DATABASE_PATH_EMAILS = "emails";

    }
    public interface DatabaseHelper {
        String DATABASE_NAME = "ProjectGuru.db3";
        //String DATABASE_NAME = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ProjectGuru/ProjectGuru.db3";
        int DATABASE_VERSION_1 = 1;
        int DATABASE_VERSION_2 = 2;
    }
    public interface LocalPrefKeys{
        String LOCAL_PREF = "_guruPrefs";
        String MASTER_DATA_FLAG = "_masterDataFlag";
        String IS_SAVED_EMAIL = "_isSavedEmail";
    }
    public interface IntentKeys {
        String IS_INTERN = "_isIntern";
        String EMAIL = "_eMail";
        String IDENTITY = "_identity";
    }
}



package com.example.root.notificationdb.ModelClass;

public class App {
    private String appName;
    private String lastTime;
    private String typeOfInstance;
    private int id;
    private String data;
    public static final String TABLE_NAME = "notifandtoastsdata";
    public static final String COLUMN_ID = "id";
    public static final String APP_NAME = "name";
    public static final String INSTANCE_TYPE = "typeofd";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String DATA = "data";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + APP_NAME + " TEXT,"
                    + DATA + " TEXT,"
                    + INSTANCE_TYPE + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeOfInstance() {
        return typeOfInstance;
    }

    public void setTypeOfInstance(String typeOfInstance) {
        this.typeOfInstance = typeOfInstance;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }


}
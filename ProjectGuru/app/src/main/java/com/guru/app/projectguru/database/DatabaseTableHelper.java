package com.guru.app.projectguru.database;

public class DatabaseTableHelper {

    public interface User {
        String TABLE_NAME = "User";
        String USER_ID = "UserID";
        String PASSWORD = "Password";
        String RGM = "RegisteredMobileNumber";
        String TMID = "TMID";
    }

    public interface OrderTypes{
        String TABLE_NAME = "OrderTypes";
        String ORDER_TYPES = "OrderType";
        String ORDER_TYPE_CODE="OrderTypeCode";

    }
    public interface Master{
        String TABLE_NAME="Master";
        String CITIES = "cities";
        String UNIVERCITIES = "universities";
        String SECTORS_KEYS = "sectorsk";
        String SECTORS_VALUES = "sectorsv";
    }
}

package com.example.offlineimagesearch.database;

class DatabaseTableHelper {

    public interface Photo {
        String TABLE_NAME = "photos_table";
        String ID = "_id";
        String SEARCH_KEY = "_search_key";
        String OWNER = "_owner";
        String SECRET = "_secret";
        String SERVER = "_server";
        String FARM = "_farm";
        String TITLE = "_title";
        String ISPUBLIC = "_is_public";
        String ISFRIEND = "_is_friend";
        String ISFAMILY = "_is_family";

    }

}

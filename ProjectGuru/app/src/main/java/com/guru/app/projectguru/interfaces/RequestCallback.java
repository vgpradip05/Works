package com.guru.app.projectguru.interfaces;

public interface RequestCallback {
    void onStart();
    void onSuccess(Object object);
    void onError(Object object);
}

package com.guru.app.projectguru.interfaces;

public interface LazyRequestCallback {
    void onStart();
    void onSuccess(Object object);
    void onError(Object object);
    void onProgress(Object object);
}

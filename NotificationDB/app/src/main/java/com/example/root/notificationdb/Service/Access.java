package com.example.root.notificationdb.Service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.example.root.notificationdb.ModelClass.Constants;
import com.example.root.notificationdb.ModelClass.DatabaseHelper;

public class Access extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        DatabaseHelper dbhelper;
        dbhelper = new DatabaseHelper(getBaseContext());
        if (event.getEventType() != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)
            return; // event is not a notification
        String sourcePackageName = (String) event.getPackageName();

        Parcelable parcelable = event.getParcelableData();
        if (parcelable instanceof Notification) {
            // Statusbar Notification
        } else {
            dbhelper.insertData(sourcePackageName, Constants.TYPE_TOAST, event.getText().get(0).toString());
        }
        dbhelper.deleteOlder();
        Intent i = new Intent(Constants.NOTIFY_BROADCAST_ACTION);
        sendBroadcast(i);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }
}
package com.example.root.notificationdb.Service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;

import com.example.root.notificationdb.ModelClass.Constants;
import com.example.root.notificationdb.ModelClass.DatabaseHelper;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotifListner extends NotificationListenerService {
    DatabaseHelper dbHelper;
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        dbHelper = new DatabaseHelper(getBaseContext());
        if(sbn.getNotification()!= null && sbn.getNotification().tickerText!=null)
            dbHelper.insertData(sbn.getPackageName(), Constants.TYPE_NOTIFICATION,sbn.getNotification().tickerText.toString());

        if(sbn.getNotification().extras!=null) {
            Bundle extras = sbn.getNotification().extras;
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)) {
                Parcelable b[] = (Parcelable[]) extras.get(Notification.EXTRA_MESSAGES);

                if (b != null) {
                    String content = Constants.EMPTY;
                    for (Parcelable tmp : b) {

                        Bundle msgBundle = (Bundle) tmp;
                        content = content + msgBundle.getString(Constants.KEY_NOTF_BUNDLE) + "\n";

                    }
                    dbHelper.insertData(sbn.getPackageName(), Constants.TYPE_NOTIFICATION,content);
                }
            }
        }

        Intent i = new Intent(Constants.NOTIFY_BROADCAST_ACTION);
        this.sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){

    }
}
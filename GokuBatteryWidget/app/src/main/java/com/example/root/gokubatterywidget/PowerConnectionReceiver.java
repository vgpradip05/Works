package com.example.root.gokubatterywidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.RemoteViews;

public class PowerConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_app_widget_provider);
        ComponentName thisWidget = new ComponentName(context, MainAppWidgetProvider.class);
        remoteViews.setImageViewResource(R.id.imagev,R.drawable.eight);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        if(level == -1){
            remoteViews.setImageViewResource(R.id.imagev,R.drawable.ten);
        }else if(level<=100 && level>= 80){
            remoteViews.setImageViewResource(R.id.imagev,R.drawable.nine);
        }else if(level<=79 && level>= 70){
            remoteViews.setImageViewResource(R.id.imagev,R.drawable.eight);
        }else if(level<=69 && level>= 60){
            remoteViews.setImageViewResource(R.id.imagev,R.drawable.seven);
        }else if(level<=59 && level>= 50){
            remoteViews.setImageViewResource(R.id.imagev,R.drawable.six);
        }else if(level<=49 && level>= 40){
            remoteViews.setImageViewResource(R.id.imagev,R.drawable.five);
        }else if(level<=39 && level>= 30){
            remoteViews.setImageViewResource(R.id.imagev,R.drawable.four);
        }else if(level<=29 && level>= 20){
            remoteViews.setImageViewResource(R.id.imagev,R.drawable.three);
        }else if(level<=19 && level>= 10){
            remoteViews.setImageViewResource(R.id.imagev,R.drawable.two);
        }else if(level<=9 && level>= 1){
            remoteViews.setImageViewResource(R.id.imagev,R.drawable.one);
        }
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }
}
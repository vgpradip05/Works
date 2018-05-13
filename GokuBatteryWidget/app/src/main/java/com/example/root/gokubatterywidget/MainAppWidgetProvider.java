package com.example.root.gokubatterywidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MainAppWidgetProvider extends AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(int i=0; i<appWidgetIds.length; i++){
            int currentWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.layout_app_widget_provider);
            context.startService(new Intent(context,TimeService.class));
            appWidgetManager.updateAppWidget(currentWidgetId,views);
        }
    }
}
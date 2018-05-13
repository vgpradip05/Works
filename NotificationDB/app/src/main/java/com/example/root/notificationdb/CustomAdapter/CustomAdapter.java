package com.example.root.notificationdb.CustomAdapter;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.notificationdb.ModelClass.App;
import com.example.root.notificationdb.ModelClass.Constants;
import com.example.root.notificationdb.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private List<App> list;
    private Activity activity;

    public CustomAdapter(List<App> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater anInflater = activity.getLayoutInflater();
        View view = anInflater.inflate(R.layout.item_layout, null);
        TextView tvAppName = view.findViewById(R.id.tv_app_name);
        TextView tvLastTime = view.findViewById(R.id.tv_last_time);
        TextView tvdata = view.findViewById(R.id.tv_app_data);
        ImageView imageView = view.findViewById(R.id.iv_icon);


        final PackageManager pm = activity.getPackageManager();
        ApplicationInfo ai;
        SimpleDateFormat formatter1 = new SimpleDateFormat(Constants.FORMATE_TIMSTAMP);
        SimpleDateFormat formatter2 = new SimpleDateFormat(Constants.FORMATE_LISTVIEW);
        Date date1;
        try {
            date1 = formatter1.parse(list.get(position).getLastTime());
            tvLastTime.setText(formatter2.format(date1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvdata.setText(list.get(position).getData());
        try {
            Drawable icon = pm.getApplicationIcon(list.get(position).getAppName());
            ai = pm.getApplicationInfo(list.get(position).getAppName(), 0);
            final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : list.get(position).getAppName());

            imageView.setImageDrawable(icon);
            tvAppName.setText(applicationName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            tvAppName.setText(activity.getString(R.string.application_deleted));
            view.setClickable(false);
            view.setFocusable(false);
            view.setFocusableInTouchMode(false);
            view.setEnabled(false);
            view.setOnClickListener(null);
            return view;
        }
        return view;
    }

}
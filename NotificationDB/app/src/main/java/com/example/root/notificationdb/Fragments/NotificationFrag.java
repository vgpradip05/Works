package com.example.root.notificationdb.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.root.notificationdb.CustomAdapter.CustomAdapter;
import com.example.root.notificationdb.ModelClass.App;
import com.example.root.notificationdb.ModelClass.Constants;
import com.example.root.notificationdb.ModelClass.DatabaseHelper;
import com.example.root.notificationdb.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationFrag extends Fragment {
    DatabaseHelper dbhelper;
    CustomAdapter customAdapter;
    List<App> list = new ArrayList<>();
    BroadcastReceiver myReciever;

    public static NotificationFrag newInstance() {
        return new NotificationFrag();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_frag, container, false);
        ListView lvApps = view.findViewById(R.id.lv_notif);
        lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                View viewDialogue = getActivity().getLayoutInflater().inflate(R.layout.view_dialogue_layout, null);
                TextView tvName = viewDialogue.findViewById(R.id.tv_app_name);
                TextView tvMsg = viewDialogue.findViewById(R.id.tv_msg_body);
                TextView tvTime = viewDialogue.findViewById(R.id.tv_time);
                ImageView ivIcon = viewDialogue.findViewById(R.id.iv_icon);
                tvMsg.setText(list.get(position).getData());
                tvTime.setText(list.get(position).getLastTime());
                Button ibInfo = viewDialogue.findViewById(R.id.bt_info);
                Button ibLaunch = viewDialogue.findViewById(R.id.bt_launch);
                Button ibDel = viewDialogue.findViewById(R.id.bt_delete);
                Drawable icon;
                final PackageManager pm = getActivity().getPackageManager();
                ApplicationInfo ai;
                try {
                    icon = pm.getApplicationIcon(list.get(position).getAppName());
                    ai = pm.getApplicationInfo(list.get(position).getAppName(), 0);
                    final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : list.get(position).getAppName());

                    ivIcon.setImageDrawable(icon);
                    tvName.setText(applicationName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                alertDialogBuilder.setView(viewDialogue);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                ibInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts(getString(R.string.package_name), list.get(position).getAppName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
                ibLaunch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        Intent i = getActivity().getPackageManager().getLaunchIntentForPackage(list.get(position).getAppName());
                        startActivity(i);
                    }
                });
                ibDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder2.setMessage(getString(R.string.delete_record_warning));

                        alertDialogBuilder2.setPositiveButton(getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        dbhelper.deleteSingleItem(list.get(position));
                                        list.remove(position);
                                        customAdapter.notifyDataSetChanged();
                                    }
                                });
                        alertDialogBuilder2.setNegativeButton(getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                    }
                                });
                        final AlertDialog alertDialog2 = alertDialogBuilder2.create();
                        alertDialog2.show();
                    }
                });
                alertDialog.show();
            }
        });
        lvApps.setEmptyView(view.findViewById(R.id.ll_no_item));
        dbhelper = new DatabaseHelper(getActivity());
        list = dbhelper.getAllApps(Constants.TYPE_NOTIFICATION);
        customAdapter = new CustomAdapter(list, getActivity());
        lvApps.setAdapter(customAdapter);
        myReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (customAdapter != null) {
                    list.clear();
                    list.addAll(dbhelper.getAllApps(Constants.TYPE_NOTIFICATION));
                    customAdapter.notifyDataSetChanged();
                }
            }
        };
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).registerReceiver(myReciever, new IntentFilter(Constants.NOTIFY_BROADCAST_ACTION));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getActivity()).unregisterReceiver(myReciever);
    }
}
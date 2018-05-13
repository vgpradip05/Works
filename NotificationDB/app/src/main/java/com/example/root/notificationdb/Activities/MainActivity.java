package com.example.root.notificationdb.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.root.notificationdb.Fragments.NotificationFrag;
import com.example.root.notificationdb.Fragments.ToastFrag;
import com.example.root.notificationdb.ModelClass.Constants;
import com.example.root.notificationdb.ModelClass.DatabaseHelper;
import com.example.root.notificationdb.R;
import com.example.root.notificationdb.Service.Access;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_CODE_ACCESSIBILITY = 0;
    private static final int REQUEST_CODE_NOTIFICATION = 1;
    Fragment selectedFragment = null;
    DatabaseHelper dbHelper;
    DrawerLayout drawer;
    SharedPreferences sharedPreferences;
    Switch swToastService, swNotifService;
    String[] listDelete = Constants.DELETE_OLDER_LIST;
    AlertDialog.Builder builder;
    int selectedItem;
    AlertDialog dialog;
    NavigationView navigationView;
    String type;
    FloatingActionButton fabDeleteAll;
    Activity context = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_with_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(Constants.APP_SHARED_PREFERENCES, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        swToastService = (Switch) navigationView.getMenu().findItem(R.id.nav_service_toast).getActionView();
        swNotifService = (Switch) navigationView.getMenu().findItem(R.id.nav_service_notif).getActionView();
        if (isNotificationServiceEnabled()) {
            swNotifService.setChecked(true);
        }
        swNotifService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                startActivityForResult(new Intent(Constants.ACTION_NOTIFICATION_LISTENER_SETTINGS), REQUEST_CODE_NOTIFICATION);
            }
        });

        if (isAccessibilitySettingsOn(context)) {
            swToastService.setChecked(true);
        }
        swToastService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, REQUEST_CODE_ACCESSIBILITY);
            }
        });
        final ArrayAdapter<String> adp = new ArrayAdapter<>(context,
                android.R.layout.select_dialog_singlechoice, listDelete);
        selectedItem = sharedPreferences.getInt(Constants.SPINNER_DATA, 0);
        builder = new AlertDialog.Builder(context)
                .setTitle(getString(R.string.select_time))
                .setSingleChoiceItems(adp, selectedItem, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItem = which;
                    }
                });
        builder.setCancelable(false);
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        selectedItem = sharedPreferences.getInt(Constants.SPINNER_DATA, 0);
                    }
                });
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        editor.putInt(Constants.SPINNER_DATA, selectedItem);
                        editor.commit();
                    }
                });
        dialog = builder.create();
        dbHelper = new DatabaseHelper(context);
        fabDeleteAll = findViewById(R.id.fab_delete_all);
        fabDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(getString(R.string.delete_multi_record_warning));
                alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (selectedFragment instanceof NotificationFrag) {
                                    dbHelper.deleteList(Constants.TYPE_NOTIFICATION);
                                } else if (selectedFragment instanceof ToastFrag) {
                                    dbHelper.deleteList(Constants.TYPE_TOAST);
                                }
                                Intent i = new Intent(Constants.NOTIFY_BROADCAST_ACTION);
                                sendBroadcast(i);
                            }
                        });
                alertDialogBuilder.setNegativeButton(getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        type = Constants.TYPE_NOTIFICATION;
        setFABVisibility();
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                selectedFragment = NotificationFrag.newInstance();
                                type = Constants.TYPE_NOTIFICATION;
                                break;
                            case R.id.action_item2:
                                selectedFragment = ToastFrag.newInstance();
                                type = Constants.TYPE_TOAST;
                                break;
                        }
                        setFABVisibility();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, NotificationFrag.newInstance());
        transaction.commit();
        startService(new Intent(context, Access.class));
        if (!isAccessibilitySettingsOn(context)) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(getString(R.string.accessibility_msg));
            alertDialogBuilder.setPositiveButton(getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            if (!sharedPreferences.getString(Constants.FIRST_RUN_FLAG, "").equals(Constants.TRUE)) {
                                editor.putString(Constants.FIRST_RUN_FLAG, Constants.TRUE);
                                editor.commit();
                                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                startActivityForResult(intent, REQUEST_CODE_ACCESSIBILITY);
                            }
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        if (!isNotificationServiceEnabled()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(getString(R.string.notification_msg));
            alertDialogBuilder.setPositiveButton(getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            if (!sharedPreferences.getString(Constants.FIRST_RUN_FLAG, Constants.EMPTY).equals(Constants.TRUE)) {
                                editor.putString(Constants.FIRST_RUN_FLAG, Constants.TRUE);
                                editor.commit();

                                startActivityForResult(new Intent(Constants.ACTION_NOTIFICATION_LISTENER_SETTINGS), REQUEST_CODE_NOTIFICATION);
                            }
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void setFABVisibility() {
        if (dbHelper.getAllApps(type).size() == 0) {
            fabDeleteAll.setVisibility(View.GONE);
        } else {
            fabDeleteAll.setVisibility(View.VISIBLE);
        }
    }
    public boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + Access.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
            //not granted
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACCESSIBILITY) {
            if (!isAccessibilitySettingsOn(context)) {
                drawer.closeDrawer(GravityCompat.START);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(getString(R.string.permission_not_granted));
                alertDialogBuilder.setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                swToastService.setChecked(false);
                if (!sharedPreferences.getString(Constants.FIRST_RUN_FLAG, Constants.EMPTY).equals(Constants.TRUE)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            } else {
                swToastService.setChecked(true);
            }
        }
        if (requestCode == REQUEST_CODE_NOTIFICATION) {
            if (!isNotificationServiceEnabled()) {
                drawer.closeDrawer(GravityCompat.START);
                swNotifService.setChecked(false);
                if (!sharedPreferences.getString(Constants.FIRST_RUN_FLAG, Constants.EMPTY).equals(Constants.TRUE)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            } else {
                swNotifService.setChecked(true);
            }
        }
    }

    private boolean isNotificationServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                Constants.ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_delete_older) {
            dialog.show();
        } else if (id == R.id.nav_delete_all) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(getString(R.string.delete_multi_record_warning));
            alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            dbHelper.deleteAll();
                            Intent i = new Intent(Constants.NOTIFY_BROADCAST_ACTION);
                            sendBroadcast(i);
                        }
                    });
            alertDialogBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else if (id == R.id.nav_share) {
            ShareCompat.IntentBuilder.from(context)
                    .setType(Constants.TEXT_PLAIN)
                    .setChooserTitle(Constants.CHOOSER_TITLE)
                    .setText(Constants.PLAY_STORE + getPackageName())
                    .startChooser();
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}
package com.guru.app.projectguru.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.guru.app.projectguru.R;
import com.guru.app.projectguru.interfaces.AlertClickListners;

public class DialogUtils {
    public static AlertDialog getSimpleDialog(final Context context, String message, final AlertClickListners alertClickListners)	{
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(context.getResources().getString(R.string.app_name));
        builder.setIcon(R.drawable.ic_launcher);
        builder.setMessage(message);
        builder.setNeutralButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertClickListners.onNeutralButtonClick();
            }
        });

        return builder.create();
    }
}

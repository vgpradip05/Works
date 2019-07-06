package com.guru.app.projectguru;

import android.app.Application;
import android.content.Context;

//import org.acra.ACRA;
//import org.acra.annotation.AcraCore;
//@AcraCore(buildConfigClass = BuildConfig.class)

public class ProjectGuru extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // The following line triggers the initialization of ACRA
        //ACRA.init(this);
    }
}

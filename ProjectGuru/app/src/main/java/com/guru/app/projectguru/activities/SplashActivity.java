package com.guru.app.projectguru.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.guru.app.projectguru.R;
import com.guru.app.projectguru.database.DataBaseUtils;
import com.guru.app.projectguru.database.FireBaseHelper;
import com.guru.app.projectguru.interfaces.RequestCallback;
import com.guru.app.projectguru.models.Identity;
import com.guru.app.projectguru.models.Master;
import com.guru.app.projectguru.utils.Constants;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        FireBaseHelper.getSnapShotFromServer(Constants.FireBasePaths.DATABASE_PATH_MASTER, new RequestCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(Object object) {
                Master master = ((DataSnapshot) object).getValue(Master.class);
                DataBaseUtils.insertMasterData(SplashActivity.this, master);
                if (mAuth.getCurrentUser() == null)
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                else {
                    identifyUserAndStartActivity();
                }
                finish();
            }

            @Override
            public void onError(Object object) {
            }
        });
    }

    private void identifyUserAndStartActivity() {
        FireBaseHelper.getSnapShotFromServer(Constants.FireBasePaths.DATABASE_PATH_EMAILS + "/" + mAuth.getCurrentUser().getEmail().split("\\.")[0], new RequestCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(Object object) {
                Identity identity = ((DataSnapshot) object).getValue(Identity.class);
                Intent intent;
                if(identity == null){
                    intent = new Intent(SplashActivity.this,InternRegistrationActivity.class);
                    intent.putExtra(Constants.IntentKeys.EMAIL,mAuth.getCurrentUser().getEmail());
                }else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra(Constants.IntentKeys.IDENTITY, identity);
                }
                startActivity(intent);
            }

            @Override
            public void onError(Object object) {
                Intent intent = new Intent(SplashActivity.this,InternRegistrationActivity.class);
                intent.putExtra(Constants.IntentKeys.EMAIL,mAuth.getCurrentUser().getEmail());
                startActivity(intent);
            }
        });
    }
}

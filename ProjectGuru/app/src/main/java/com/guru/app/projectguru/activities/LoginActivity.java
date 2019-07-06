package com.guru.app.projectguru.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.guru.app.projectguru.R;
import com.guru.app.projectguru.database.FireBaseHelper;
import com.guru.app.projectguru.interfaces.RequestCallback;
import com.guru.app.projectguru.models.Identity;
import com.guru.app.projectguru.utils.AnimateUtils;
import com.guru.app.projectguru.utils.Constants;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1002;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    SignInButton btGSignIn;
    Button btSignIn;
    EditText etEmail, etPassword;
    TextInputLayout tilEmail, tilPassword;
    View progressOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initVars();
    }

    private void initVars() {
        mAuth = FirebaseAuth.getInstance();
        progressOverlay = findViewById(R.id.progress_overlay_layout);
        btGSignIn = findViewById(R.id.bt_g_sign_in);
        btSignIn = findViewById(R.id.bt_act_login_sign_in);
        etEmail = findViewById(R.id.et_act_login_email);
        etPassword = findViewById(R.id.et_act_login_password);
        tilEmail = findViewById(R.id.input_layout_act_login_email);
        tilPassword = findViewById(R.id.input_act_login_password);
        btGSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(new GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(Constants.GoogleSignInKeys.REQUEST_ID_TOKEN)
                        .requestEmail()
                        .build());
            }
        });
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSubmit();
            }
        });
    }

    private void validateAndSubmit() {
        if (!validateEmailNo()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        createUser();
    }

    private void signInUser() {
        AnimateUtils.showLoadingView(progressOverlay);
        mAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            AnimateUtils.hideLoadingView(progressOverlay);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            identifyUserAndStartActivity();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            AnimateUtils.hideLoadingView(progressOverlay);
                        }
                        // ...
                    }
                });
    }

    private void createUser() {
        AnimateUtils.showLoadingView(progressOverlay);
        mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            identifyUserAndStartActivity();
                            AnimateUtils.hideLoadingView(progressOverlay);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            if(task.getException().getMessage().equalsIgnoreCase("The email address is already in use by another account."))
                                signInUser();
                            AnimateUtils.hideLoadingView(progressOverlay);
                        }
                        // ...
                    }
                });
    }

    private void signIn(GoogleSignInOptions gso) {
        android.content.Intent signInIntent = GoogleSignIn.getClient(this, gso).getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            identifyUserAndStartActivity();
                            Log.d("MyTAG", "onComplete: " + (isNew ? "new user" : "old user"));
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        // ...
                    }
                });
    }

    private boolean validateEmailNo() {
        if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError(Constants.ErrorMessages.EMPTY_EMAIL);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches()) {
            etEmail.setError(Constants.ErrorMessages.INVALID_EMAIL);
            return false;
        } else {
            tilEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (TextUtils.isEmpty(etPassword.getText())) {
            etPassword.setError(Constants.ErrorMessages.EMPTY_FIELD);
            return false;
        } else if (etPassword.getText().toString().length() < 6 || etPassword.getText().toString().length() > 12) {
            etPassword.setError(Constants.ErrorMessages.INVALID_PASSWORD);
            return false;
        } else {
            tilPassword.setErrorEnabled(false);
        }
        return true;
    }

    private void identifyUserAndStartActivity() {
        AnimateUtils.showLoadingView(progressOverlay);
        FireBaseHelper.getSnapShotFromServer(Constants.FireBasePaths.DATABASE_PATH_EMAILS + "/" + mAuth.getCurrentUser().getEmail().split("\\.")[0], new RequestCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(Object object) {
                AnimateUtils.hideLoadingView(progressOverlay);
                Identity identity = ((DataSnapshot) object).getValue(Identity.class);
                Intent intent;
                if(identity == null){
                    intent = new Intent(LoginActivity.this,InternRegistrationActivity.class);
                    intent.putExtra(Constants.IntentKeys.EMAIL,mAuth.getCurrentUser().getEmail());
                }else {
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(Constants.IntentKeys.IDENTITY, identity);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Object object) {
                AnimateUtils.hideLoadingView(progressOverlay);
                Intent intent = new Intent(LoginActivity.this,InternRegistrationActivity.class);
                intent.putExtra(Constants.IntentKeys.EMAIL,mAuth.getCurrentUser().getEmail());
                startActivity(intent);
                finish();
            }
        });
    }
}

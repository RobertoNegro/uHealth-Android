package com.negroroberto.uhealth.activities.abstracts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.Task;
import com.negroroberto.uhealth.utils.Debug;

public abstract class UHealthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void closeActivity() {
        onClosing();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onClosing();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onClosing();
    }

    protected void onClosing() {

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeActivity();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    //region Google & GFIT
    private GoogleSignInListener mGoogleSignInListener = null;
    private GoogleSignInAccount mAccount = null;
    public void signInWithGoogle(GoogleSignInListener listener) {
        if(mGoogleSignInListener != null)
            mGoogleSignInListener.onSignIn(null);
        mAccount = null;

        if(listener != null) {
            mGoogleSignInListener = listener;

            mAccount = GoogleSignIn.getLastSignedInAccount(this);
            if (mAccount != null)
                getFitPermissions();
            else {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, REQUEST_SIGN_IN);
            }
        }
    }

    private void getFitPermissions() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)

                .build();

        if (!GoogleSignIn.hasPermissions(mAccount, fitnessOptions))
            GoogleSignIn.requestPermissions(this, REQUEST_GOOGLE_FIT, mAccount, fitnessOptions);
        else {
            mGoogleSignInListener.onSignIn(mAccount);
            mGoogleSignInListener = null;
            mAccount = null;
        }
    }

    private static final int REQUEST_SIGN_IN = 100;
    private static final int REQUEST_GOOGLE_FIT = 101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case REQUEST_SIGN_IN: {
                if (mGoogleSignInListener != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        mAccount = task.getResult(ApiException.class);
                        getFitPermissions();
                    } catch (ApiException e) {
                        Debug.Err(this, "Failed getting signed in account: " + e.toString());
                    }
                }
            } break;
            case REQUEST_GOOGLE_FIT: {
                if(resultCode == Activity.RESULT_OK) {
                    mGoogleSignInListener.onSignIn(mAccount);
                    mGoogleSignInListener = null;
                    mAccount = null;
                }
            } break;
        }
    }
    //endregion
}

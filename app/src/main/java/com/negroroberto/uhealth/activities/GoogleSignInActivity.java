package com.negroroberto.uhealth.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.activities.abstracts.GoogleSignInListener;
import com.negroroberto.uhealth.activities.abstracts.UHealthActivity;
import com.negroroberto.uhealth.services.CoachingService;
import com.negroroberto.uhealth.tts.PollyTTS;

import java.net.URL;

public class GoogleSignInActivity extends UHealthActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        signInWithGoogle(new GoogleSignInListener() {
            @Override
            public void onSignIn(GoogleSignInAccount account) {
                Intent serviceIntent = new Intent(GoogleSignInActivity.this, CoachingService.class);
                serviceIntent.putExtra(CoachingService.ACTION, CoachingService.ACTION_START);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    startForegroundService(serviceIntent);
                else
                    startService(serviceIntent);

                finish();
            }
        });
    }
}
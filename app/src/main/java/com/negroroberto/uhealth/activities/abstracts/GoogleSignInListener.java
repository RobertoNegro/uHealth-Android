package com.negroroberto.uhealth.activities.abstracts;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface GoogleSignInListener {
    void onSignIn(GoogleSignInAccount account);
}
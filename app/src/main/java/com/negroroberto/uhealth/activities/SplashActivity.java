package com.negroroberto.uhealth.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.abstracts.UHealthActivity;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.Extras;
import com.negroroberto.uhealth.utils.SharedPrefsCodes;

public class SplashActivity extends UHealthActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SceneActivity.class);

        String avatarMessage = getIntent().getStringExtra(Extras.EXTRA_AVATAR_MESSAGE);
        if(avatarMessage != null)
            intent.putExtra(Extras.EXTRA_AVATAR_MESSAGE, avatarMessage);

        boolean firstRun = Common.getSharedPreferences(this).getBoolean(SharedPrefsCodes.KEY_FIRST_RUN, true);
        //firstRun = true; //TODO: FORCE first run

        if(firstRun)
            intent.putExtra(Extras.EXTRA_FIRST_RUN, true);

        startActivity(intent);

        finish();
    }
}

package com.negroroberto.uhealth.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.abstracts.UHealthActivity;
import com.negroroberto.uhealth.activities.abstracts.UnityActivity;
import com.negroroberto.uhealth.databinding.ActivityTestBinding;
import com.negroroberto.uhealth.unity.UnityController;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.Extras;
import com.negroroberto.uhealth.utils.SharedPrefsCodes;

public class TestActivity extends UnityActivity implements UnityController.EventListener {
    private ActivityTestBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        setResult(RESULT_CANCELED);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mBinding.idUnity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, SceneActivity.class);
                startActivity(intent);
            }
        });

        mBinding.btnResetFirstRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = Common.getSharedPreferences(TestActivity.this).edit();
                editor.putBoolean(SharedPrefsCodes.KEY_FIRST_RUN, true);
                editor.apply();
            }
        });
    }

    @Override
    public void setUnitySettings() {
        super.setUnitySettings();

        ((UHealthApplication)getApplication()).getUnityController().setCameraTarget(UnityController.CameraTarget.HEAD);
        ((UHealthApplication)getApplication()).getUnityController().setCamera(180, 0.4f, 0.9f);
    }

    private boolean mFirstTime = true;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && mFirstTime) {
            mFirstTime = false;
            View parent = (View) mBinding.idUnity.getParent();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.idUnity.getLayoutParams();
            params.leftMargin = (int) parent.getWidth() - mBinding.idUnity.getWidth() - 30;
            params.topMargin = (int) parent.getHeight() - mBinding.idUnity.getHeight() - 30;
            mBinding.idUnity.setLayoutParams(params);
        }
    }

    public boolean onSupportNavigateUp() {
        closeActivity();
        return true;
    }

    @Override
    public void onUnityLoaded() {

    }

    @Override
    public void onAvatarLoading() {

    }

    @Override
    public void onAvatarLoaded() {

    }
}
package com.negroroberto.uhealth.modules;

import android.content.Context;

import com.negroroberto.uhealth.UHealthApplication;

public abstract class Module {
    private UHealthApplication mApplication;

    public Module(UHealthApplication application) {
        mApplication = application;
    }

    public UHealthApplication getApplication() {
        return mApplication;
    }

    public void setApplication(UHealthApplication application) {
        mApplication = application;
    }
}

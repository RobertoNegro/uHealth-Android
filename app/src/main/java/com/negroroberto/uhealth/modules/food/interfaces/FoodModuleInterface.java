package com.negroroberto.uhealth.modules.food.interfaces;

import android.content.Context;

public abstract class FoodModuleInterface {
    private Context mContext;
    public FoodModuleInterface(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public abstract void getByBarcode(final String barcode, final FoodResultListener listener);
}

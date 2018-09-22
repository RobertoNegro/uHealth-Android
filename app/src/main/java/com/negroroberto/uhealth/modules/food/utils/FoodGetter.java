package com.negroroberto.uhealth.modules.food.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.negroroberto.uhealth.modules.food.interfaces.FoodModuleInterface;
import com.negroroberto.uhealth.modules.food.interfaces.FoodResultListener;
import com.negroroberto.uhealth.modules.food.models.Food;
import com.negroroberto.uhealth.modules.food.modules.OpenFoodFactsModule;
import com.negroroberto.uhealth.modules.food.modules.USDAModule;
import com.negroroberto.uhealth.utils.Debug;

import java.util.ArrayList;

public class FoodGetter {
    private ArrayList<FoodModuleInterface> modules;
    private Context mContext;
    private FoodResultListener mResultListener;

    public FoodGetter(Context context) {
        mContext = context;
        modules = new ArrayList<>();
        modules.add(new OpenFoodFactsModule(mContext));
        modules.add(new USDAModule(mContext));
    }

    public FoodResultListener getResultListener() {
        return mResultListener;
    }

    public void setResultListener(FoodResultListener listener) {
        mResultListener = listener;
    }

    public void startSearch(String barcode) {
        Debug.Log(this, "Starting search for: " + barcode);
        search(barcode, 0);
    }

    private void search(final String barcode, final int index) {
        Debug.Log(this, "Search module index: " + index);
        if(index >= modules.size()) {
            if(mResultListener != null)
                mResultListener.onResult(null, null);
            return;
        }

        final FoodModuleInterface module = modules.get(index);

        module.getByBarcode(barcode, new FoodResultListener() {
            @Override
            public void onResult(Food food, Bitmap image) {
                if (food != null && food.getCode().equals(barcode)) {
                    Debug.Log(this, "Found with " + module.getClass().getSimpleName() + ": " + food.toString());
                    if(mResultListener != null)
                        mResultListener.onResult(food, image);
                } else {
                    Debug.Log(this, "Not found with " + module.getClass().getSimpleName());
                    search(barcode, index+1);
                }
            }
        });
    }
}

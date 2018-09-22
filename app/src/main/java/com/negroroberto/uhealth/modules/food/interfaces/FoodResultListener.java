package com.negroroberto.uhealth.modules.food.interfaces;

import android.graphics.Bitmap;

import com.negroroberto.uhealth.modules.food.models.Food;

public interface FoodResultListener {
    void onResult(Food food, Bitmap image);
}

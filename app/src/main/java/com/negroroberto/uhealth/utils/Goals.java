package com.negroroberto.uhealth.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Goals {
    private static final String PREF_KEY = "com.negroroberto.uhealth.prefs.goals";
    private SharedPreferences mSharedPref;

    private static final String PREF_FOOD_CALORIES = "com.negroroberto.uhealth.prefs.goals.food.calories";
    private float mFoodCalories;
    private static final String PREF_FOOD_CARBS = "com.negroroberto.uhealth.prefs.goals.food.carbs";
    private float mFoodCarbs;
    private static final String PREF_FOOD_FAT = "com.negroroberto.uhealth.prefs.goals.food.fat";
    private float mFoodFat;
    private static final String PREF_FOOD_PROTEIN = "com.negroroberto.uhealth.prefs.goals.food.protein";
    private float mFoodProtein;
    private static final String PREF_WATER_QUANTITY = "com.negroroberto.uhealth.prefs.goals.water.quantity";
    private float mWaterQuantity;
    private static final String PREF_SPORT_DURATION = "com.negroroberto.uhealth.prefs.goals.sport.duration";
    private float mSportDuration;
    private static final String PREF_SPORT_DISTANCE = "com.negroroberto.uhealth.prefs.goals.sport.distance";
    private float mSportDistance;
    private static final String PREF_SPORT_CALORIES = "com.negroroberto.uhealth.prefs.goals.sport.calories";
    private float mSportCalories;
    private static final String PREF_SPORT_STEPS = "com.negroroberto.uhealth.prefs.goals.sport.steps";
    private float mSportSteps;

    public Goals(Context context) {
        mSharedPref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);

        mFoodCalories = mSharedPref.getFloat(PREF_FOOD_CALORIES, 0);
        mFoodCarbs = mSharedPref.getFloat(PREF_FOOD_CARBS, 0);
        mFoodFat = mSharedPref.getFloat(PREF_FOOD_FAT, 0);
        mFoodProtein = mSharedPref.getFloat(PREF_FOOD_PROTEIN, 0);

        mWaterQuantity = mSharedPref.getFloat(PREF_WATER_QUANTITY, 0);

        mSportDuration = mSharedPref.getFloat(PREF_SPORT_DURATION, 0);
        mSportDistance = mSharedPref.getFloat(PREF_SPORT_DISTANCE, 0);
        mSportCalories = mSharedPref.getFloat(PREF_SPORT_CALORIES, 0);
        mSportSteps = mSharedPref.getFloat(PREF_SPORT_STEPS, 0);
    }

    public float getFoodCalories() {
        return mFoodCalories;
    }

    public void setFoodCalories(float foodCalories) {
        mFoodCalories = foodCalories;

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putFloat(PREF_FOOD_CALORIES, mFoodCalories);
        editor.apply();
    }

    public float getFoodCarbs() {
        return mFoodCarbs;
    }

    public void setFoodCarbs(float foodCarbs) {
        mFoodCarbs = foodCarbs;

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putFloat(PREF_FOOD_CARBS, mFoodCarbs);
        editor.apply();
    }

    public float getFoodFat() {
        return mFoodFat;
    }

    public void setFoodFat(float foodFat) {
        mFoodFat = foodFat;

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putFloat(PREF_FOOD_FAT, mFoodFat);
        editor.apply();
    }

    public float getFoodProtein() {
        return mFoodProtein;
    }

    public void setFoodProtein(float foodProtein) {
        mFoodProtein = foodProtein;

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putFloat(PREF_FOOD_PROTEIN, mFoodProtein);
        editor.apply();
    }

    public float getWaterQuantity() {
        return mWaterQuantity;
    }

    public void setWaterQuantity(float waterQuantity) {
        mWaterQuantity = waterQuantity;

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putFloat(PREF_WATER_QUANTITY, mWaterQuantity);
        editor.apply();
    }

    public float getSportDuration() {
        return mSportDuration;
    }

    public void setSportDuration(float sportDuration) {
        mSportDuration = sportDuration;

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putFloat(PREF_SPORT_DURATION, mSportDuration);
        editor.apply();
    }

    public float getSportDistance() {
        return mSportDistance;
    }

    public void setSportDistance(float sportDistance) {
        mSportDistance = sportDistance;

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putFloat(PREF_SPORT_DISTANCE, mSportDistance);
        editor.apply();
    }

    public float getSportCalories() {
        return mSportCalories;
    }

    public void setSportCalories(float sportCalories) {
        mSportCalories = sportCalories;

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putFloat(PREF_SPORT_CALORIES, mSportCalories);
        editor.apply();
    }

    public float getSportSteps() {
        return mSportSteps;
    }

    public void setSportSteps(float sportSteps) {
        mSportSteps = sportSteps;

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putFloat(PREF_SPORT_STEPS, mSportSteps);
        editor.apply();
    }
}

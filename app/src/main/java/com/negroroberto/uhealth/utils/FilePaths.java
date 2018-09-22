package com.negroroberto.uhealth.utils;

import android.content.Context;

public class FilePaths {
    public static String GetFoodImagePath(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath() + "/food/imgs/";
    }
}

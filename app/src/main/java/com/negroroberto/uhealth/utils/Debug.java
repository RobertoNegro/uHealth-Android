package com.negroroberto.uhealth.utils;

import android.util.Log;

public class Debug {
    public static void Log(Object o, String message) {
        Log.d(GetTag(o), message);
    }

    public static void Warn(Object o, String message) {
        Log.w(GetTag(o), message);
    }

    public static void Err(Object o, String message) {
        Log.e(GetTag(o), message);
    }

    private static String GetTag(Object o) {
        String classname = ((o != null) ? (o.getClass().getName()) : ("null"));
        if(classname.lastIndexOf('.') >= 0)
            classname = classname.substring(classname.lastIndexOf('.')+1);
        return "DBG_" + classname;
    }
}

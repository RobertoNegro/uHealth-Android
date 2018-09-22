package com.negroroberto.uhealth.db;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Converters {
    @TypeConverter
    public static Calendar TimestampToCalendar(Long value) {
        if(value == null)
            return null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(value));
        return cal;
    }

    @TypeConverter
    public static Long CalendarToTimestamp(Calendar calendar) {
       if(calendar == null)
           return null;

       return calendar.getTimeInMillis();
    }

    @TypeConverter
    public static ArrayList<Long> StringToIdList(String value) {
        Type listType = new TypeToken<ArrayList<Long>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String IdListToString(ArrayList<Long> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

}

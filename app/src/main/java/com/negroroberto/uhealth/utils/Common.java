package com.negroroberto.uhealth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.negroroberto.uhealth.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Common {
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SharedPrefsCodes.NAME, Context.MODE_PRIVATE);
    }


    public static int DpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }


    public static double ParseDouble(String s) throws NumberFormatException, NullPointerException {
        String sClean = s.trim().replaceAll("\\s+", "").replace(',', '.');
        return Double.parseDouble(sClean);
    }


    public static String DoubleStringify(double d) {
        String res = String.format(Locale.getDefault(), "%.2f", d);
        res = res.replaceAll("0+$", "").replaceAll(",$", "").replaceAll("\\.$", "");
        return res;
    }

    public static Bitmap GetBitmapFromFile(Context context, String path) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        if (bitmap == null)
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_photo_default);

        return bitmap;
    }

    public static Calendar GetCalendarFromISO(String dateTimeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;
        try {
            date = sdf.parse(dateTimeString);
        } catch (ParseException e) {
            Debug.Err(Common.class, "Error parsing date: " + e.toString());
        }

        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }

        return cal;
    }


    public static Calendar GetCalendarTimeFromISO(String timeString) {
        //LocalDate localDate = LocalDate.parse(dateString);
        //OffsetDateTime time = OffsetDateTime.parse(timeString);
        //ZonedDateTime zonedDateTime = ZonedDateTime.of(time, ZoneId.systemDefault());

        String[] separated = timeString.split("\\:");

        int hours;
        int minutes;

        try {
            hours = Integer.parseInt(separated[0]);
        } catch (NumberFormatException e) {
            hours = 12;
        }

        try {
            minutes = Integer.parseInt(separated[1]);
        } catch (NumberFormatException e) {
            minutes = 30;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 0);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    public static Calendar GetCalendarDateFromISO(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            Debug.Err(Common.class, "Error parsing date: " + e.toString());
        }

        Calendar cal = Calendar.getInstance();

        if (date != null) {
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }

        return cal;



        /*

         */
    }
}

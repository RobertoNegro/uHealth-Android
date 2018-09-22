package com.negroroberto.uhealth.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.negroroberto.uhealth.services.CoachingService;

import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        StartCoachingService(context);
    }

    public static void StartCoachingService(Context context){
        Intent serviceIntent = new Intent(context, CoachingService.class);
        serviceIntent.putExtra(CoachingService.ACTION, CoachingService.ACTION_START);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(serviceIntent);
        else
            context.startService(serviceIntent);
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction("com.negroroberto.uhealth.alarms");
        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static boolean IsAlarmSet(Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction("com.negroroberto.uhealth.alarms");
        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null;
    }

    public static void SetAlarm(Context context) {
        if(!IsAlarmSet(context)) {
            PendingIntent pendingIntent = getPendingIntent(context);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Calendar morning = Calendar.getInstance();
            morning.setTime(new Date());
            morning.set(Calendar.HOUR_OF_DAY, 0);
            morning.set(Calendar.MINUTE, 0);
            morning.set(Calendar.SECOND, 0);
            morning.set(Calendar.MILLISECOND, 0);

            int everyHours = 4;
            if (manager != null)
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, morning.getTimeInMillis(), 1000 * 60 * 60 * everyHours, pendingIntent);
        }
    }

    public static void RemoveAlarm(Context context) {
        PendingIntent pendingIntent = getPendingIntent(context);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(manager != null)
            manager.cancel(pendingIntent);
    }
}
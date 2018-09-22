package com.negroroberto.uhealth.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.GoogleSignInActivity;
import com.negroroberto.uhealth.activities.SplashActivity;
import com.negroroberto.uhealth.modules.food.FoodModule;
import com.negroroberto.uhealth.modules.food.models.Meal;
import com.negroroberto.uhealth.modules.sport.GoogleFitModule;
import com.negroroberto.uhealth.modules.water.WaterModule;
import com.negroroberto.uhealth.modules.water.models.WaterDrunk;
import com.negroroberto.uhealth.utils.Debug;
import com.negroroberto.uhealth.utils.Extras;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CoachingService extends Service {
    public static final String ACTION = "ACTION";
    public static final String ACTION_START = "ACTION_START";
    public static final String RESPONSE = "RESPONSE";
    public static final String RESPONSE_NOTIFICATION = "RESPONSE_NOTIFICATION";
    public static final String RESPONSE_BROADCAST = "RESPONSE_BROADCAST";

    public static final String BROADCAST_ACTION = "COACHING_RESULT";
    public static final String BROADCAST_EXTRA_MESSAGE = "BROADCAST_EXTRA_MESSAGE";

    public static final String NOTIFICATION_CHANNEL_ID = "UHEALTH_NOTIFICATION_COACHING_CHANNEL_ID";
    public static final String NOTIFICATION_CHANNEL_NAME = "uHealth - Coaching";
    public static final int NOTIFICATION_ID_CHECK = 101;
    public static final int NOTIFICATION_ID_SCENE = 102;

    private boolean mIsRunning;

    private FoodModule mFoodModule;
    private WaterModule mWaterModule;
    private GoogleFitModule mSportModule;

    private Calendar mStartDay;
    private Calendar mEndDay;
    private double mTimeRatio;

    private boolean mHasSomethingToSay;
    private String mMessage;

    private boolean mResponseNotification;
    private boolean mResponseBroadcast;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
        chan.enableLights(true);
        chan.setLightColor(ContextCompat.getColor(this, R.color.darkAccent));
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        chan.setImportance(NotificationManager.IMPORTANCE_HIGH);

        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (service != null)
            service.createNotificationChannel(chan);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String action = intent.getStringExtra(ACTION);
        String response = intent.getStringExtra(RESPONSE);
        if(response == null)
            response = "";
        switch (response) {
            case RESPONSE_BROADCAST:
                mResponseBroadcast = true;
                mResponseNotification = false;
                break;
            default:
            case RESPONSE_NOTIFICATION:
                mResponseBroadcast = false;
                mResponseNotification = true;
                break;
        }

        if (action != null) {
            switch (action) {
                case ACTION_START: {
                    if (!mIsRunning) {
                        mIsRunning = true;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            createNotificationChannel();

                        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                        if (account == null) {
                            Intent notificationIntent = new Intent(CoachingService.this, GoogleSignInActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(CoachingService.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            NotificationCompat.Builder notSceneBuilder = new NotificationCompat.Builder(CoachingService.this, NOTIFICATION_CHANNEL_ID);
                            notSceneBuilder.setAutoCancel(true)
                                    .setSmallIcon(R.drawable.ic_notification_logo)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setWhen(System.currentTimeMillis())
                                    .setColor(ContextCompat.getColor(this, R.color.darkAccent))
                                    .setTicker("uHealth")
                                    .setContentTitle("Google sign in required!")
                                    .setContentText("To check your sport stats, Alice needs your help! Tap on this notification and allow her to check your Google Fit data.")
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("To check your sport stats, Alice needs your help! Tap on this notification and allow her to check your Google Fit data."))
                                    .setOngoing(false)
                                    .setContentIntent(pendingIntent);

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                            notificationManager.notify(NOTIFICATION_ID_SCENE, notSceneBuilder.build());

                            mIsRunning = false;
                            stopSelf();
                        } else {
                            NotificationCompat.Builder notificationBuilderCheck = new NotificationCompat.Builder(CoachingService.this, NOTIFICATION_CHANNEL_ID);
                            notificationBuilderCheck.setAutoCancel(true)
                                    .setSmallIcon(R.drawable.ic_notification_logo)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setWhen(System.currentTimeMillis())
                                    .setColor(ContextCompat.getColor(this, R.color.darkAccent))
                                    .setTicker("uHealth")
                                    .setContentTitle("Analyzing data")
                                    .setContentText("Alice, your personal assistant, is analyzing your recorded data. Please, wait...")
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Alice, your personal assistant, is analyzing your recorded data. Please, wait..."))
                                    .setOngoing(true);

                            startForeground(NOTIFICATION_ID_CHECK, notificationBuilderCheck.build());

                            final Calendar cal = Calendar.getInstance();
                            cal.setTime(new Date());
                            final int actHour = cal.get(Calendar.HOUR_OF_DAY);
                            final int actMinutes = cal.get(Calendar.MINUTE);

                            final int startHour = 6;
                            final int startMinutes = 0;
                            final int endHour = 23;
                            final int endMinutes = 0;
                            final int totalMinutes = (endHour * 60 + endMinutes) - (startHour * 60 + startMinutes);

                            int minutes;
                            if (actHour > startHour) {
                                minutes = (actHour - startHour) * 60 - startMinutes + actMinutes;
                            } else if (actHour == startHour && actMinutes >= startMinutes) {
                                minutes = actMinutes - startMinutes;
                            } else
                                minutes = 0;

                            mTimeRatio = (double) minutes / totalMinutes;
                            if (mTimeRatio > 1)
                                mTimeRatio = 1;

                            Debug.Log(this, "Time ratio: " + mTimeRatio);

                            mStartDay = Calendar.getInstance();
                            mStartDay.setTime(new Date());
                            mStartDay.set(Calendar.HOUR_OF_DAY, 0);
                            mStartDay.set(Calendar.MINUTE, 0);
                            mStartDay.set(Calendar.SECOND, 0);
                            mStartDay.set(Calendar.MILLISECOND, 0);


                            mEndDay = Calendar.getInstance();
                            mEndDay.setTime(new Date());
                            mEndDay.set(Calendar.HOUR_OF_DAY, 23);
                            mEndDay.set(Calendar.MINUTE, 59);
                            mEndDay.set(Calendar.SECOND, 59);
                            mEndDay.set(Calendar.MILLISECOND, 999);


                            mFoodModule = new FoodModule(((UHealthApplication)getApplication()));
                            mWaterModule = new WaterModule(((UHealthApplication)getApplication()));
                            mSportModule = new GoogleFitModule(((UHealthApplication)getApplication()), account);

                            mHasSomethingToSay = false;
                            mMessage = "";

                            getFoodStats(new OnFoodResultListener() {
                                @Override
                                public void onResult(final OkUnderOver foodCalories, final OkUnderOver foodCarbs, final OkUnderOver foodFat, final OkUnderOver foodProtein) {
                                    getWaterStats(new OnWaterResultListener() {
                                        @Override
                                        public void onResult(final boolean waterQuantity) {
                                            getSportStats(new OnSportResultListener() {
                                                @Override
                                                public void onResult(final boolean sportDistance, final boolean sportDuration, final boolean sportCalories, final boolean sportStep) {
                                                    int foodOver = 0;
                                                    if (foodCalories == OkUnderOver.OVER)
                                                        foodOver++;
                                                    if (foodCarbs == OkUnderOver.OVER)
                                                        foodOver++;
                                                    if (foodFat == OkUnderOver.OVER)
                                                        foodOver++;
                                                    if (foodProtein == OkUnderOver.OVER)
                                                        foodOver++;

                                                    int foodUnder = 0;
                                                    if (foodCalories == OkUnderOver.UNDER)
                                                        foodUnder++;
                                                    if (foodCarbs == OkUnderOver.UNDER)
                                                        foodUnder++;
                                                    if (foodFat == OkUnderOver.UNDER)
                                                        foodUnder++;
                                                    if (foodProtein == OkUnderOver.UNDER)
                                                        foodUnder++;

                                                    StringBuilder messageBuilder = new StringBuilder();

                                                    if (!sportCalories || !sportDistance || !sportDuration || !sportStep) {
                                                        messageBuilder.append("When you will have some free time, I suggest you to do some physical activity to reach your sport's goals");
                                                        if (foodOver > 0)
                                                            messageBuilder.append(" and to compensate your overtaking of your ");
                                                        else
                                                            messageBuilder.append("!");
                                                    }


                                                    if (foodOver > 0) {
                                                        if (!sportCalories && !sportDistance && !sportDuration && !sportStep)
                                                            messageBuilder.append("When you will have some free time, I suggest you to go out and do some physical activity, since you're overtaking your ");

                                                        int tmpFoodOver = 0;
                                                        for (int i = 0; i < 4; i++) {
                                                            String name = "";
                                                            OkUnderOver value = OkUnderOver.OK;
                                                            switch (i) {
                                                                case 0:
                                                                    name = "calories";
                                                                    value = foodCalories;
                                                                    break;
                                                                case 1:
                                                                    name = "carbohydrates";
                                                                    value = foodCarbs;
                                                                    break;
                                                                case 2:
                                                                    name = "fat";
                                                                    value = foodFat;
                                                                    break;
                                                                case 3:
                                                                    name = "protein";
                                                                    value = foodProtein;
                                                                    break;
                                                            }

                                                            if (value == OkUnderOver.OVER) {
                                                                tmpFoodOver++;
                                                                if (tmpFoodOver == 1)
                                                                    messageBuilder.append("");
                                                                else if (tmpFoodOver == foodOver)
                                                                    messageBuilder.append(" and ");
                                                                else
                                                                    messageBuilder.append(", ");
                                                                messageBuilder.append(name);
                                                            }
                                                        }
                                                        messageBuilder.append(" goal!");
                                                    }

                                                    if (foodUnder > 0) {
                                                        if (foodOver > 0 || !sportCalories || !sportDistance || !sportDuration || !sportStep)
                                                            messageBuilder.append(" But don't forget to integrate with a correct dose of ");
                                                        else
                                                            messageBuilder.append("Actually you're low on your ");

                                                        int tmpFoodUnder = 0;
                                                        for (int i = 0; i < 4; i++) {
                                                            String name = "";
                                                            OkUnderOver value = OkUnderOver.OK;
                                                            switch (i) {
                                                                case 0:
                                                                    name = "calories";
                                                                    value = foodCalories;
                                                                    break;
                                                                case 1:
                                                                    name = "carbohydrates";
                                                                    value = foodCarbs;
                                                                    break;
                                                                case 2:
                                                                    name = "fat";
                                                                    value = foodFat;
                                                                    break;
                                                                case 3:
                                                                    name = "protein";
                                                                    value = foodProtein;
                                                                    break;
                                                            }

                                                            if (value == OkUnderOver.UNDER) {
                                                                tmpFoodUnder++;
                                                                if (tmpFoodUnder == 1)
                                                                    messageBuilder.append("");
                                                                else if (tmpFoodUnder == foodUnder)
                                                                    messageBuilder.append(" and ");
                                                                else
                                                                    messageBuilder.append(", ");
                                                                messageBuilder.append(name);
                                                            }
                                                        }
                                                        if (foodOver > 0 || !sportCalories || !sportDistance || !sportDuration || !sportStep) {
                                                            messageBuilder.append(", since you're low on ");
                                                            if (foodOver == 1)
                                                                messageBuilder.append("that.");
                                                            else
                                                                messageBuilder.append("those.");
                                                        } else
                                                            messageBuilder.append(" intake!");

                                                        messageBuilder.append(" Remember that is important to try to reach, at the end of the day, a value near your goal, without exceeding or going beneath too much. I suggest you to eat something appropriate to compensate!");
                                                    }

                                                    if (!waterQuantity) {
                                                        if (foodOver > 0 || foodUnder > 0 || !sportCalories || !sportDistance || !sportDuration || !sportStep)
                                                            messageBuilder.append(" And try");
                                                        else
                                                            messageBuilder.append(" Try");

                                                        messageBuilder.append(" to drink more water! Drinking water is important to hydrate your body!");
                                                    }

                                                    mMessage = messageBuilder.toString();
                                                    if (foodOver > 0 || foodUnder > 0 || !sportCalories || !sportDistance || !sportDuration || !sportStep || !waterQuantity)
                                                        mHasSomethingToSay = true;
                                                    else
                                                        mMessage = "You're going well! Keep on!";

                                                    finishCoaching();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
                break;
            }
        }

        return START_STICKY;
    }

    private enum OkUnderOver {
        OK, UNDER, OVER
    }

    private interface OnFoodResultListener {
        void onResult(final OkUnderOver foodCalories, final OkUnderOver foodCarbs, final OkUnderOver foodFat, final OkUnderOver foodProtein);
    }

    private interface OnWaterResultListener {
        void onResult(final boolean waterQuantity);
    }

    private interface OnSportResultListener {
        void onResult(final boolean sportDistance, final boolean sportDuration, final boolean sportCalories, final boolean sportStep);
    }

    private void getFoodStats(final OnFoodResultListener listener) {
        mFoodModule.getMealsByPeriod(mStartDay.getTimeInMillis(), mEndDay.getTimeInMillis(), new FoodModule.OnMealsListResultListener() {
            @Override
            public void onResult(final ArrayList<Meal> result) {
                getFoodStats(result, listener, 0, OkUnderOver.UNDER, OkUnderOver.UNDER, OkUnderOver.UNDER, OkUnderOver.UNDER);
            }
        });
    }

    private void getFoodStats(final ArrayList<Meal> meals, final OnFoodResultListener listener, final int index, final OkUnderOver calories, final OkUnderOver carbs, final OkUnderOver fat, final OkUnderOver protein) {
        switch (index) {
            case 0:
                mFoodModule.getMealListTotalCalories(meals, new FoodModule.OnDoubleResultListener() {
                    @Override
                    public void onResult(final double result) {
                        double overGoal = ((UHealthApplication)getApplication()).getGoals().getFoodCalories() * mTimeRatio * 1.25;
                        double underGoal = ((UHealthApplication)getApplication()).getGoals().getFoodCalories() * mTimeRatio * 0.75;
                        if (((UHealthApplication)getApplication()).getGoals().getFoodCalories() == 0)
                            overGoal = Double.MAX_VALUE;

                        Debug.Log(this, "Food Calories: " + underGoal + " <= " + result + " <= " + overGoal + " ?");

                        OkUnderOver underOver;
                        if (result < underGoal)
                            underOver = OkUnderOver.UNDER;
                        else if (result > overGoal)
                            underOver = OkUnderOver.OVER;
                        else
                            underOver = OkUnderOver.OK;

                        getFoodStats(meals, listener, index + 1, underOver, carbs, fat, protein);
                    }
                });
                break;
            case 1:
                mFoodModule.getMealListTotalCarbs(meals, new FoodModule.OnDoubleResultListener() {
                    @Override
                    public void onResult(final double result) {
                        double overGoal = ((UHealthApplication)getApplication()).getGoals().getFoodCarbs() * mTimeRatio * 1.25;
                        double underGoal = ((UHealthApplication)getApplication()).getGoals().getFoodCarbs() * mTimeRatio * 0.75;
                        if (((UHealthApplication)getApplication()).getGoals().getFoodCarbs() == 0)
                            overGoal = Double.MAX_VALUE;

                        Debug.Log(this, "Food Carbs: " + underGoal + " <= " + result + " <= " + overGoal + " ?");

                        OkUnderOver underOver;
                        if (result < underGoal)
                            underOver = OkUnderOver.UNDER;
                        else if (result > overGoal)
                            underOver = OkUnderOver.OVER;
                        else
                            underOver = OkUnderOver.OK;

                        getFoodStats(meals, listener, index + 1, calories, underOver, fat, protein);
                    }
                });
                break;
            case 2:
                mFoodModule.getMealListTotalFat(meals, new FoodModule.OnDoubleResultListener() {
                    @Override
                    public void onResult(final double result) {
                        double overGoal = ((UHealthApplication)getApplication()).getGoals().getFoodFat() * mTimeRatio * 1.25;
                        double underGoal = ((UHealthApplication)getApplication()).getGoals().getFoodFat() * mTimeRatio * 0.75;
                        if (((UHealthApplication)getApplication()).getGoals().getFoodFat() == 0)
                            overGoal = Double.MAX_VALUE;

                        Debug.Log(this, "Food Fat: " + underGoal + " <= " + result + " <= " + overGoal + " ?");

                        OkUnderOver underOver;
                        if (result < underGoal)
                            underOver = OkUnderOver.UNDER;
                        else if (result > overGoal)
                            underOver = OkUnderOver.OVER;
                        else
                            underOver = OkUnderOver.OK;

                        getFoodStats(meals, listener, index + 1, calories, carbs, underOver, protein);
                    }
                });
                break;
            case 3:
                mFoodModule.getMealListTotalProtein(meals, new FoodModule.OnDoubleResultListener() {
                    @Override
                    public void onResult(final double result) {
                        double overGoal = ((UHealthApplication)getApplication()).getGoals().getFoodProtein() * mTimeRatio * 1.25;
                        double underGoal = ((UHealthApplication)getApplication()).getGoals().getFoodProtein() * mTimeRatio * 0.75;
                        if (((UHealthApplication)getApplication()).getGoals().getFoodProtein() == 0)
                            overGoal = Double.MAX_VALUE;

                        Debug.Log(this, "Food Protein: " + underGoal + " <= " + result + " <= " + overGoal + " ?");

                        OkUnderOver underOver;
                        if (result < underGoal)
                            underOver = OkUnderOver.UNDER;
                        else if (result > overGoal)
                            underOver = OkUnderOver.OVER;
                        else
                            underOver = OkUnderOver.OK;

                        getFoodStats(meals, listener, index + 1, calories, carbs, fat, underOver);
                    }
                });
                break;
            default:
                if (listener != null)
                    listener.onResult(calories, carbs, fat, protein);
                break;
        }
    }

    private void getWaterStats(final OnWaterResultListener listener) {
        mWaterModule.getWaterByPeriod(mStartDay.getTimeInMillis(), mEndDay.getTimeInMillis(), new WaterModule.OnWaterDrunkListResultListener() {
            @Override
            public void onResult(ArrayList<WaterDrunk> result) {
                getWaterStats(result, listener, 0, false);
            }
        });
    }

    private void getWaterStats(final ArrayList<WaterDrunk> waterDrunks, final OnWaterResultListener listener, final int index, final boolean quantity) {
        switch (index) {
            case 0:
                long result = 0;
                for (WaterDrunk wd : waterDrunks)
                    result += wd.getQuantity();

                double goal = ((UHealthApplication)getApplication()).getGoals().getWaterQuantity() * 1000 * mTimeRatio * 0.75;
                Debug.Log(this, "Water Quantity: " + result + " >= " + goal + " ?");

                getWaterStats(waterDrunks, listener, index + 1, result >= goal);
                break;
            default:
                if (listener != null)
                    listener.onResult(quantity);
                break;
        }
    }

    private void getSportStats(final OnSportResultListener listener) {
        getSportStats(listener, 0, false, false, false, false);
    }

    private void getSportStats(final OnSportResultListener listener, final int index, final boolean distance, final boolean duration, final boolean calories, final boolean steps) {
        switch (index) {
            case 0:
                mSportModule.getDistance(mStartDay.getTimeInMillis(), mEndDay.getTimeInMillis(), new GoogleFitModule.OnFloatResultListener() {
                    @Override
                    public void onResult(final float result) {
                        double goal = ((UHealthApplication)getApplication()).getGoals().getSportDistance() * 1000 * mTimeRatio * 0.75;
                        Debug.Log(this, "Sport Distance: " + result + " >= " + goal + " ?");

                        getSportStats(listener, index + 1, result >= goal, duration, calories, steps);
                    }
                });
                break;
            case 1:
                mSportModule.getDuration(mStartDay.getTimeInMillis(), mEndDay.getTimeInMillis(), new GoogleFitModule.OnLongResultListener() {
                    @Override
                    public void onResult(final long result) {
                        double goal = ((UHealthApplication)getApplication()).getGoals().getSportDuration() * mTimeRatio * 0.75;
                        Debug.Log(this, "Sport Duration: " + result + " >= " + goal + " ?");

                        getSportStats(listener, index + 1, distance, result >= goal, calories, steps);
                    }
                });
                break;
            case 2:
                mSportModule.getCalories(mStartDay.getTimeInMillis(), mEndDay.getTimeInMillis(), new GoogleFitModule.OnFloatResultListener() {
                    @Override
                    public void onResult(final float result) {
                        double goal = ((UHealthApplication)getApplication()).getGoals().getSportCalories() * mTimeRatio * 0.75;
                        Debug.Log(this, "Sport Calories: " + result + " >= " + goal + " ?");

                        getSportStats(listener, index + 1, distance, duration, result >= goal, steps);
                    }
                });
                break;
            case 3:
                mSportModule.getSteps(mStartDay.getTimeInMillis(), mEndDay.getTimeInMillis(), new GoogleFitModule.OnLongResultListener() {
                    @Override
                    public void onResult(final long result) {
                        double goal = ((UHealthApplication)getApplication()).getGoals().getSportSteps() * mTimeRatio * 0.75;
                        Debug.Log(this, "Sport Steps: " + result + " >= " + goal + " ?");

                        getSportStats(listener, index + 1, distance, duration, calories, result >= goal);
                    }
                });
                break;
            default:
                if (listener != null)
                    listener.onResult(distance, duration, calories, steps);
                break;
        }
    }


    private void finishCoaching() {
        stopForeground(true);

        if (mResponseNotification && mHasSomethingToSay) {
            Intent notificationIntent = new Intent(CoachingService.this, SplashActivity.class);
            notificationIntent.putExtra(Extras.EXTRA_AVATAR_MESSAGE, mMessage);

            PendingIntent pendingIntent = PendingIntent.getActivity(CoachingService.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notSceneBuilder = new NotificationCompat.Builder(CoachingService.this, NOTIFICATION_CHANNEL_ID);
            notSceneBuilder.setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_notification_logo)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setColor(ContextCompat.getColor(this, R.color.darkAccent))
                    .setTicker("uHealth")
                    .setContentTitle("Alice has some tips!")
                    .setContentText("Alice, your personal assistant, has something to say to you. Tap to see!")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Alice, your personal assistant, has something to say to you. Tap to see!"))
                    .setOngoing(false)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NOTIFICATION_ID_SCENE, notSceneBuilder.build());
        }

        if (mResponseBroadcast) {
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra(BROADCAST_EXTRA_MESSAGE, mMessage);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

        mIsRunning = false;
        stopSelf();
    }
}

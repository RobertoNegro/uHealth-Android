package com.negroroberto.uhealth.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.abstracts.UnityActivity;
import com.negroroberto.uhealth.ai.DialogFlow;
import com.negroroberto.uhealth.databinding.ActivitySceneBinding;
import com.negroroberto.uhealth.modules.food.FoodModule;
import com.negroroberto.uhealth.modules.food.models.Food;
import com.negroroberto.uhealth.modules.food.models.Meal;
import com.negroroberto.uhealth.modules.water.WaterModule;
import com.negroroberto.uhealth.modules.water.models.WaterDrunk;
import com.negroroberto.uhealth.receivers.AlarmReceiver;
import com.negroroberto.uhealth.services.CoachingService;
import com.negroroberto.uhealth.unity.UnityController;
import com.negroroberto.uhealth.utils.AfterTextChangedListener;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.Debug;
import com.negroroberto.uhealth.utils.Extras;
import com.negroroberto.uhealth.utils.RequestCodes;
import com.negroroberto.uhealth.utils.SharedPrefsCodes;
import com.negroroberto.uhealth.utils.ViewAnimation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ai.api.model.AIResponse;
import cn.dreamtobe.messagehandler.MessageHandler;

public class SceneActivity extends UnityActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private ActivitySceneBinding mBinding;

    private SpeechRecognizer mSpeechRecognizer;
    private DialogFlow mDialogFlow;

    private boolean mEditStateEmpty;
    private boolean mCardVisibility;

    private FoodModule mFoodModule;
    private WaterModule mWaterModule;

    private boolean mFirstRun;
    private boolean mFirstRunAddFood;
    private boolean mFirstRunAddMeal;
    private boolean mFirstRunAddWater;

    private CardStatusListener mCardStatusListener;

    private MessageHandler mHandler;

    private void firstRunStepOne() {
        sendMessageToUser("Hi, I'm Alice, nice to meet you! I'm here to help you reaching your goals thanks to the analysis of all your food, water and sport data. First, you should set your goals.", new UnityController.SpeechStatusListener() {
            @Override
            public void onStart(String path) {

            }

            @Override
            public void onEnd(String path) {
                final Intent intent = new Intent(SceneActivity.this, GoalSettingsActivity.class);
                intent.putExtra(Extras.EXTRA_FIRST_RUN, true);
                startActivityForResult(intent, RequestCodes.REQUEST_GOAL_SETTINGS);
            }

            @Override
            public void onError() {

            }
        });
    }

    private void firstRunStepTwo() {
        sendMessageToUser("Well done! By the way, you can ask me to change those goals whenever you want! From now, I will check your data and try to help you reaching your set values. I can automatically check your sports data, but I need your help for both your nutrition and hydration values.", new UnityController.SpeechStatusListener() {
            @Override
            public void onStart(String path) {

            }

            @Override
            public void onEnd(String path) {
                firstRunStepThree();
            }

            @Override
            public void onError() {

            }
        });
    }


    private void firstRunStepThree() {
        sendMessageToUser("For the insertion of your nutritional data, you have first to ask me to record a new type of food. Let's try!", new UnityController.SpeechStatusListener() {
            @Override
            public void onStart(String path) {

            }

            @Override
            public void onEnd(String path) {
                mFirstRunAddFood = true;
                switchToInputType();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendMessageToUser("Try to ask me to add a new type of food!", null);
                        mHandler.postDelayed(this, SystemClock.uptimeMillis() + 30000);
                    }
                }, SystemClock.uptimeMillis() + 30000);
            }

            @Override
            public void onError() {

            }
        });
    }

    private void firstRunStepFour(boolean done) {
        String message;
        if (done)
            message = "Well done, I've just recorder your first type of food!";
        else
            message = "Don't worry, you can do it whenever you want, it's not important to do it now.";

        sendMessageToUser(message, new UnityController.SpeechStatusListener() {
            @Override
            public void onStart(String path) {

            }

            @Override
            public void onEnd(String path) {
                sendMessageToUser("Now, ask me to record a new meal!", new UnityController.SpeechStatusListener() {
                    @Override
                    public void onStart(String path) {

                    }

                    @Override
                    public void onEnd(String path) {
                        mFirstRunAddMeal = true;
                        switchToInputType();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendMessageToUser("Try to ask me to record a new meal!", null);
                                mHandler.postDelayed(this, SystemClock.uptimeMillis() + 30000);
                            }
                        }, SystemClock.uptimeMillis() + 30000);
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            @Override
            public void onError() {

            }
        });
    }

    private void firstRunStepFive() {
        sendMessageToUser("Last but not least, for the insertion of your hydration data, you can ask me to remember when you have drunk some water. It's easy: try it now!", new UnityController.SpeechStatusListener() {
            @Override
            public void onStart(String path) {

            }

            @Override
            public void onEnd(String path) {
                mFirstRunAddWater = true;
                switchToInputType();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendMessageToUser("Try to tell me that you've drunk water!", null);
                        mHandler.postDelayed(this, SystemClock.uptimeMillis() + 30000);
                    }
                }, SystemClock.uptimeMillis() + 30000);
            }

            @Override
            public void onError() {

            }
        });
    }

    private void firstRunStepSix(boolean done) {
        String message;
        if (done)
            message = "Well done, I've just recorder your first water consumption!";
        else
            message = "Don't worry, you can do it whenever you want, it's not important to do it now.";

        sendMessageToUser(message, new UnityController.SpeechStatusListener() {
            @Override
            public void onStart(String path) {

            }

            @Override
            public void onEnd(String path) {
                sendMessageToUser("That's all! If you want, you can ask me to show you all your statistics. I can make a summary and show you some graphs too. From now, remember to keep updated all your data. I'll check periodically if you're going well on your goals. If something is going wrong, I will definitely tell you!", new UnityController.SpeechStatusListener() {
                    @Override
                    public void onStart(String path) {

                    }

                    @Override
                    public void onEnd(String path) {
                        SharedPreferences.Editor editor = Common.getSharedPreferences(SceneActivity.this).edit();
                        editor.putBoolean(SharedPrefsCodes.KEY_FIRST_RUN, false);
                        editor.apply();

                        switchToInputType();
                        setMenuVisibility(true, true);
                        sendMessageToUser("How can I help you?", null);
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            @Override
            public void onError() {

            }
        });
    }

    /*

    private void firstRunStepFive() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final ImageView imageView = new ImageView(SceneActivity.this);
        imageView.setLayoutParams(params);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(SceneActivity.this)
                        .load(R.raw.gif_drank_water)
                        .into(imageView);
                mBinding.frameCard.addView(imageView);
            }
        });

        setCardVisibility(true, true);
        mCardStatusListener = new CardStatusListener() {
            @Override
            public void onShow() {

            }

            @Override
            public void onHide() {
                mCardStatusListener = null;
                firstRunStepFive();
            }
        };
    }
    */

    @Override
    public void onClick(View v) {
        if (v.equals(mBinding.btnCardClose)) {
            setCardVisibility(false, true);
        } else if (v.equals(mBinding.btnMenu)) {
            mBinding.drawerLayout.openDrawer(GravityCompat.START);
        } else if (v.equals(mBinding.btnRecord)) {
            switchToVoiceInput();
            ((UHealthApplication) getApplication()).getUnityController().shutup();

            if (mFirstRunAddFood || mFirstRunAddMeal || mFirstRunAddWater)
                mHandler.cancelAllMessage();

            startVoiceRecognition();
        } else if (v.equals(mBinding.btnKeyboard)) {
            switchToTextInput("");
        } else if (v.equals(mBinding.btnFinishVoice)) {
            mSpeechRecognizer.stopListening();
            switchToTextInput(mBinding.editInputText.getText().toString());
        } else if (v.equals(mBinding.btnSend)) {
            String text = mBinding.editInputText.getText().toString().trim();
            if (text.length() > 0)
                sendMessageToAvatar(text);
            switchToInputType();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String avatarMessage = getIntent().getStringExtra(Extras.EXTRA_AVATAR_MESSAGE);
        if (avatarMessage != null && avatarMessage.trim().length() > 0)
            sendMessageToUser(avatarMessage, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scene);

        mFirstRun = getIntent().getBooleanExtra(Extras.EXTRA_FIRST_RUN, false);
        if (mFirstRun)
            firstRunStepOne();

        String avatarMessage = getIntent().getStringExtra(Extras.EXTRA_AVATAR_MESSAGE);
        if (avatarMessage != null && avatarMessage.trim().length() > 0)
            sendMessageToUser(avatarMessage, null);


        mFoodModule = new FoodModule((UHealthApplication) getApplication());
        mWaterModule = new WaterModule((UHealthApplication) getApplication());

        mDialogFlow = new DialogFlow();

        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // fix per barra scura in basso al navdrawer causato dal fullscreen di unity in loading
        mBinding.navView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                return insets;
            }
        });

        mBinding.navView.setNavigationItemSelectedListener(this);

        setGradientVisibility(false, false);
        setMenuVisibility(false, false);
        setLoadingVisibility(false, false);
        setCardVisibility(false, false);
        setContentVisibility(false, false);

        if (mFirstRun)
            switchToHideAll();
        else
            switchToInputType();


        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new UnitySpeechRecognitionListener());

        mEditStateEmpty = true;
        final TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                getDrawable(R.drawable.btn_close),
                getDrawable(R.drawable.btn_send)
        });
        td.setCrossFadeEnabled(true);
        mBinding.btnSend.setImageDrawable(td);

        mBinding.btnSend.setOnClickListener(this);
        mBinding.btnMenu.setOnClickListener(this);
        mBinding.btnCardClose.setOnClickListener(this);
        mBinding.btnRecord.setOnClickListener(this);
        mBinding.btnKeyboard.setOnClickListener(this);
        mBinding.btnFinishVoice.setOnClickListener(this);

        mBinding.editInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    mBinding.btnSend.performClick();
                    return true;
                }
                return false;
            }
        });

        mBinding.messageScroll.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                scrollMessageToBottom();
            }
        });

        mBinding.editInputText.addTextChangedListener(new AfterTextChangedListener() {
            @Override
            protected void afterTextChanged(String s) {
                if (s != null) {
                    if (s.trim().length() > 0 && mEditStateEmpty) {
                        td.startTransition(150);
                        mEditStateEmpty = false;
                    } else if (s.trim().length() == 0 && !mEditStateEmpty) {
                        td.reverseTransition(150);
                        mEditStateEmpty = true;
                    }
                } else {
                    if (!mEditStateEmpty) {
                        td.reverseTransition(150);
                        mEditStateEmpty = true;
                    }
                }
            }
        });

        mHandler = new MessageHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.pause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RequestCodes.REQUEST_STATS: {
                sendDoneQuery();
            }
            break;

            case RequestCodes.REQUEST_FOOD_CREATION: {
                if (resultCode == RESULT_OK) {
                    Food f = data.getParcelableExtra(Extras.EXTRA_FOOD);
                    mFoodModule.createFood(new FoodModule.OnLongResultListener() {
                        @Override
                        public void onResult(long result) {
                            if (mFirstRunAddFood) {
                                mFirstRunAddFood = false;
                                firstRunStepFour(true);
                            } else
                                sendDoneQuery();
                        }
                    }, f);
                } else {
                    if (mFirstRunAddFood) {
                        mFirstRunAddFood = false;
                        firstRunStepFour(false);
                    } else
                        sendCancelQuery();
                }
            }
            break;

            case RequestCodes.REQUEST_MEAL_DETAILS: {
                if (mFirstRunAddMeal) {
                    mFirstRunAddMeal = false;
                    firstRunStepFive();
                } else
                    sendDoneQuery();
            }
            break;

            case RequestCodes.REQUEST_WATER_CREATION: {
                if (resultCode == RESULT_OK && data != null) {
                    WaterDrunk water = data.getParcelableExtra(Extras.EXTRA_WATER);
                    if (water != null) {
                        mWaterModule.createWater(new WaterModule.OnLongResultListener() {
                            @Override
                            public void onResult(long result) {
                                if (mFirstRunAddWater) {
                                    mFirstRunAddWater = false;
                                    firstRunStepSix(true);
                                } else
                                    sendDoneQuery();
                            }
                        }, water);
                    }
                } else {
                    if (mFirstRunAddWater) {
                        mFirstRunAddWater = false;
                        firstRunStepSix(false);
                    } else
                        sendCancelQuery();
                }
            }
            break;

            case RequestCodes.REQUEST_GOAL_SETTINGS: {
                if (!mFirstRun)
                    sendDoneQuery();
                else
                    firstRunStepTwo();
            }
            break;

            case RequestCodes.REQUEST_SUMMARY: {
                sendDoneQuery();
            }
            break;

            case RequestCodes.REQUEST_TEST: {
            }
            break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mBinding.drawerLayout.closeDrawers();

        switch (menuItem.getItemId()) {
            case R.id.menu_stats: {
                Intent intent = new Intent(SceneActivity.this, StatsActivity.class);
                startActivityForResult(intent, RequestCodes.REQUEST_STATS);
            }
            break;
            case R.id.menu_summary: {
                Intent intent = new Intent(SceneActivity.this, SummaryActivity.class);
                startActivityForResult(intent, RequestCodes.REQUEST_SUMMARY);
            }
            break;
            case R.id.menu_coaching: {
                AlarmReceiver.StartCoachingService(SceneActivity.this);
            }
            break;
            case R.id.menu_test: {
                Intent intent = new Intent(SceneActivity.this, TestActivity.class);
                startActivityForResult(intent, RequestCodes.REQUEST_TEST);
            }
            break;
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCardVisibility)
                setCardVisibility(false, true);
            else
                closeActivity();
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        quitUnity();
    }

    private void startVoiceRecognition() {
        String language = Locale.ENGLISH.toString();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{language});

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        mSpeechRecognizer.startListening(intent);
    }

    //region frame layout functions
    private void setGradientVisibility(final boolean visible, final boolean anim) {
        ViewAnimation.SetViewVisibility(this, mBinding.gradientOverlay, visible, anim);
    }

    private void setContentVisibility(final boolean visible, final boolean anim) {
        ViewAnimation.SetViewVisibility(this, mBinding.contentOverlay, visible, anim);
    }

    private void setMenuVisibility(final boolean visible, final boolean anim) {
        ViewAnimation.SetViewVisibility(this, mBinding.menuOverlay, visible, anim);
    }

    private void setLoadingVisibility(final boolean visible, final boolean anim) {
        ViewAnimation.SetViewVisibility(this, mBinding.loadingOverlay, visible, anim);
    }

    private void setInputTypeVisibility(final boolean visible, final boolean anim) {
        ViewAnimation.SetViewVisibility(this, mBinding.inputTypeContainer, visible, anim);
    }

    private void setInputTextVisibility(final boolean visible, final boolean anim) {
        ViewAnimation.SetViewVisibility(this, mBinding.inputTextContainer, visible, anim);
    }

    private void setInputVoiceVisibility(final boolean visible, final boolean anim) {
        ViewAnimation.SetViewVisibility(this, mBinding.inputVoiceContainer, visible, anim);
    }

    private void setCardVisibility(final boolean visible, final boolean anim) {
        ViewAnimation.SetViewSlide(this, mBinding.cardOverlay, visible, anim);
        mCardVisibility = visible;
        if (mCardStatusListener != null) {
            if (mCardVisibility)
                mCardStatusListener.onShow();
            else
                mCardStatusListener.onHide();
        }
    }

    private void switchToTextInput(String text) {
        setInputTypeVisibility(false, true);
        setInputVoiceVisibility(false, true);

        setInputTextVisibility(true, true);
        mBinding.editInputText.setText(text != null ? text : "");

        mBinding.editInputText.requestFocus();
        mBinding.editInputText.setSelection(mBinding.editInputText.getText().length());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(mBinding.editInputText, InputMethodManager.SHOW_IMPLICIT);

    }

    private void switchToVoiceInput() {
        setInputTypeVisibility(false, true);
        setInputTextVisibility(false, true);

        setInputVoiceVisibility(true, true);

        mBinding.txtVoiceInput.setText("");
        mBinding.editInputText.setText("");
        mBinding.txtVoiceInput.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(mBinding.editInputText.getWindowToken(), 0);
    }

    private void switchToInputType() {
        setInputTextVisibility(false, true);
        setInputVoiceVisibility(false, true);

        setInputTypeVisibility(true, true);

        mBinding.btnRecord.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(mBinding.editInputText.getWindowToken(), 0);
    }

    private void switchToHideAll() {
        setInputTextVisibility(false, true);
        setInputVoiceVisibility(false, true);
        setInputTypeVisibility(false, true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(mBinding.editInputText.getWindowToken(), 0);
    }
    //endregion


    //region DialogFlow
    private void sendDoneQuery() {
        mDialogFlow.query("I've finished", new DialogFlow.OnResponseListener() {
            @Override
            public void onResponse(AIResponse response) {
                sendMessageToUser(response.getResult().getFulfillment().getSpeech(), null);
            }
        });
    }

    private void sendCancelQuery() {
        mDialogFlow.query("I've canceled", new DialogFlow.OnResponseListener() {
            @Override
            public void onResponse(AIResponse response) {
                sendMessageToUser(response.getResult().getFulfillment().getSpeech(), null);
            }
        });
    }

    private void analyzeMessage(String message) {
        DialogFlow.OnResponseListener listener;
        if (mFirstRunAddFood) {
            listener = new DialogFlow.OnResponseListener() {
                @Override
                public void onResponse(AIResponse response) {
                    String action = response.getResult().getAction();
                    switch (action) {
                        case "food-record":
                        case "food-record-barcode": {
                            mHandler.cancelAllMessage();
                            switchToHideAll();

                            final Intent intent = new Intent(SceneActivity.this, FoodCreationActivity.class);
                            intent.putExtra(Extras.EXTRA_FIRST_RUN, true);
                            sendMessageToUser(response.getResult().getFulfillment().getSpeech(), new UnityController.SpeechStatusListener() {
                                @Override
                                public void onStart(String path) {

                                }

                                @Override
                                public void onEnd(String path) {
                                    startActivityForResult(intent, RequestCodes.REQUEST_FOOD_CREATION);
                                }

                                @Override
                                public void onError() {

                                }
                            });
                        }
                        break;
                        default: {
                            sendMessageToUser("Can you ask me to add a new type of food, please?", null);
                            mHandler.cancelAllMessage();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sendMessageToUser("Try to ask me to add a new type of food!", null);
                                    mHandler.postDelayed(this, SystemClock.uptimeMillis() + 30000);
                                }
                            }, SystemClock.uptimeMillis() + 30000);
                        }
                        break;
                    }
                }
            };
        } else if (mFirstRunAddMeal) {
            listener = new DialogFlow.OnResponseListener() {
                @Override
                public void onResponse(AIResponse response) {
                    String action = response.getResult().getAction();
                    switch (action) {
                        case "meal-record":
                        case "meal-record-date":
                        case "meal-record-time":
                        case "meal-record-datetime": {
                            mHandler.cancelAllMessage();
                            switchToHideAll();

                            final Calendar date = response.getResult().getParameters().containsKey("date") ? Common.GetCalendarDateFromISO(response.getResult().getParameters().get("date").getAsString()) : null;
                            final Calendar time = response.getResult().getParameters().containsKey("time") ? Common.GetCalendarTimeFromISO(response.getResult().getParameters().get("time").getAsString()) : null;

                            if (action.equals("meal-record") || (action.equals("meal-record-date") && date != null) || (action.equals("meal-record-time") && time != null) || (action.equals("meal-record-datetime") && date != null && time != null)) {
                                final Calendar cal = Calendar.getInstance();

                                if (date != null) {
                                    cal.set(Calendar.YEAR, date.get(Calendar.YEAR));
                                    cal.set(Calendar.MONTH, date.get(Calendar.MONTH));
                                    cal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                                } else
                                    cal.setTime(new Date());

                                if (time != null) {
                                    cal.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
                                    cal.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
                                } else if (date != null) {
                                    cal.set(Calendar.HOUR_OF_DAY, 12);
                                    cal.set(Calendar.MINUTE, 30);
                                }

                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MILLISECOND, 0);

                                final Meal newMeal = new Meal();

                                newMeal.setName("Untitled");
                                newMeal.setTime(cal);

                                sendMessageToUser(response.getResult().getFulfillment().getSpeech(), new UnityController.SpeechStatusListener() {
                                    @Override
                                    public void onStart(String path) {

                                    }

                                    @Override
                                    public void onEnd(String path) {
                                        mFoodModule.createMeal(new FoodModule.OnLongResultListener() {
                                            @Override
                                            public void onResult(long result) {
                                                newMeal.setId(result);
                                                Intent intent = new Intent(SceneActivity.this, MealDetailsActivity.class);
                                                intent.putExtra(Extras.EXTRA_MEAL, (Parcelable) newMeal);
                                                intent.putExtra(Extras.EXTRA_FIRST_RUN, true);
                                                startActivityForResult(intent, RequestCodes.REQUEST_MEAL_DETAILS);
                                            }
                                        }, newMeal);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                            }
                        }
                        break;
                        default: {
                            sendMessageToUser("Can you ask me to record a new meal, please?", null);
                            mHandler.cancelAllMessage();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sendMessageToUser("Try to ask me to record a new meal!", null);
                                    mHandler.postDelayed(this, SystemClock.uptimeMillis() + 30000);
                                }
                            }, SystemClock.uptimeMillis() + 30000);
                        }
                        break;
                    }


                }
            };
        } else if (mFirstRunAddWater) {
            listener = new DialogFlow.OnResponseListener() {
                @Override
                public void onResponse(AIResponse response) {
                    String action = response.getResult().getAction();
                    switch (action) {
                        case "water-record":
                        case "water-record-time":
                        case "water-record-date":
                        case "water-record-datetime": {
                            mHandler.cancelAllMessage();
                            switchToHideAll();

                            final Calendar date = response.getResult().getParameters().containsKey("date") ? Common.GetCalendarDateFromISO(response.getResult().getParameters().get("date").getAsString()) : null;
                            final Calendar time = response.getResult().getParameters().containsKey("time") ? Common.GetCalendarTimeFromISO(response.getResult().getParameters().get("time").getAsString()) : null;

                            if (action.equals("water-record") || (action.equals("water-record-time") && time != null) || (action.equals("water-record-date") && date != null) || (action.equals("water-record-datetime") && time != null && date != null)) {
                                final Calendar cal = Calendar.getInstance();

                                if (date != null) {
                                    cal.set(Calendar.YEAR, date.get(Calendar.YEAR));
                                    cal.set(Calendar.MONTH, date.get(Calendar.MONTH));
                                    cal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                                } else {
                                    cal.setTime(new Date());
                                }

                                if (time != null) {
                                    cal.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
                                    cal.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
                                } else {
                                    if (date != null) {
                                        cal.set(Calendar.HOUR_OF_DAY, 12);
                                        cal.set(Calendar.MINUTE, 30);
                                    }
                                }

                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MILLISECOND, 0);


                                final Intent intent = new Intent(SceneActivity.this, WaterCreationActivity.class);
                                intent.putExtra(Extras.EXTRA_CALENDAR, cal);
                                intent.putExtra(Extras.EXTRA_FIRST_RUN, true);
                                sendMessageToUser(response.getResult().getFulfillment().getSpeech(), new UnityController.SpeechStatusListener() {
                                    @Override
                                    public void onStart(String path) {

                                    }

                                    @Override
                                    public void onEnd(String path) {
                                        startActivityForResult(intent, RequestCodes.REQUEST_WATER_CREATION);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                            }
                        }
                        break;
                        default: {
                            sendMessageToUser("Can you tell me that you've drunk some water, please?", null);
                            mHandler.cancelAllMessage();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sendMessageToUser("Try to tell me that you've drunk water!", null);
                                    mHandler.postDelayed(this, SystemClock.uptimeMillis() + 30000);
                                }
                            }, SystemClock.uptimeMillis() + 30000);
                        }
                        break;
                    }
                }
            };
        } else {
            listener = new DialogFlow.OnResponseListener() {
                @Override
                public void onResponse(AIResponse response) {
                    Debug.Log(this, "Received DialogFlow Response: " + response.getResult().getFulfillment().getSpeech());
                    Debug.Log(this, "Action: " + response.getResult().getAction());
                    if (response.getResult().getParameters().size() > 0) {
                        Debug.Log(this, "Parameters: ");
                        for (String key : response.getResult().getParameters().keySet())
                            Debug.Log(this, key + " : " + response.getResult().getParameters().get(key));
                    }

                    Runnable afterTmp = null;

                    String action = response.getResult().getAction();
                    switch (action) {
                        case "food-record":
                        case "food-record-barcode": {
                            final Intent intent = new Intent(SceneActivity.this, FoodCreationActivity.class);

                            if (action.equals("food-record-barcode"))
                                intent.putExtra(Extras.EXTRA_FOOD_CREATION_BARCODE, true);

                            afterTmp = new Runnable() {
                                @Override
                                public void run() {
                                    startActivityForResult(intent, RequestCodes.REQUEST_FOOD_CREATION);
                                }
                            };
                        }
                        break;

                        case "goals-set": {
                            final Intent intent = new Intent(SceneActivity.this, GoalSettingsActivity.class);
                            afterTmp = new Runnable() {
                                @Override
                                public void run() {
                                    startActivityForResult(intent, RequestCodes.REQUEST_GOAL_SETTINGS);
                                }
                            };
                        }
                        break;


                        case "meal-record":
                        case "meal-record-date":
                        case "meal-record-time":
                        case "meal-record-datetime": {
                            final Calendar date = response.getResult().getParameters().containsKey("date") ? Common.GetCalendarDateFromISO(response.getResult().getParameters().get("date").getAsString()) : null;
                            final Calendar time = response.getResult().getParameters().containsKey("time") ? Common.GetCalendarTimeFromISO(response.getResult().getParameters().get("time").getAsString()) : null;

                            if (action.equals("meal-record") || (action.equals("meal-record-date") && date != null) || (action.equals("meal-record-time") && time != null) || (action.equals("meal-record-datetime") && date != null && time != null)) {
                                final Calendar cal = Calendar.getInstance();

                                if (date != null) {
                                    cal.set(Calendar.YEAR, date.get(Calendar.YEAR));
                                    cal.set(Calendar.MONTH, date.get(Calendar.MONTH));
                                    cal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                                } else
                                    cal.setTime(new Date());

                                if (time != null) {
                                    cal.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
                                    cal.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
                                } else if (date != null) {
                                    cal.set(Calendar.HOUR_OF_DAY, 12);
                                    cal.set(Calendar.MINUTE, 30);
                                }

                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MILLISECOND, 0);

                                final Meal newMeal = new Meal();
                                newMeal.setName("Untitled");
                                newMeal.setTime(cal);

                                afterTmp = new Runnable() {
                                    @Override
                                    public void run() {
                                        mFoodModule.createMeal(new FoodModule.OnLongResultListener() {
                                            @Override
                                            public void onResult(long result) {
                                                newMeal.setId(result);
                                                Intent intent = new Intent(SceneActivity.this, MealDetailsActivity.class);
                                                intent.putExtra(Extras.EXTRA_MEAL, (Parcelable) newMeal);
                                                startActivityForResult(intent, RequestCodes.REQUEST_MEAL_DETAILS);
                                            }
                                        }, newMeal);
                                    }
                                };
                            }
                        }
                        break;

                        case "stats-show":
                        case "stats-show-module":
                        case "stats-show-date":
                        case "stats-show-moduledate": {
                            final String module = response.getResult().getParameters().containsKey("module") ? response.getResult().getParameters().get("module").getAsString().toLowerCase().trim() : null;
                            final Calendar date = response.getResult().getParameters().containsKey("date") ? Common.GetCalendarDateFromISO(response.getResult().getParameters().get("date").getAsString()) : null;

                            if (action.equals("stats-show") || (action.equals("stats-show-module") && module != null) || (action.equals("stats-show-date") && date != null) || (action.equals("stats-show-moduledate") && module != null && date != null)) {
                                final Intent intent = new Intent(SceneActivity.this, StatsActivity.class);

                                if (module != null) {
                                    switch (module) {
                                        case "food":
                                            intent.putExtra(Extras.EXTRA_STATS_FORCE_SHOW_MODULE, StatsActivity.Module.FOOD);
                                            break;
                                        case "water":
                                            intent.putExtra(Extras.EXTRA_STATS_FORCE_SHOW_MODULE, StatsActivity.Module.WATER);
                                            break;
                                        case "sport":
                                            intent.putExtra(Extras.EXTRA_STATS_FORCE_SHOW_MODULE, StatsActivity.Module.SPORT);
                                            break;
                                    }
                                }

                                if (date != null) {
                                    Calendar act = Calendar.getInstance();
                                    act.setTime(new Date());
                                    while (date.compareTo(act) > 0)
                                        date.add(Calendar.YEAR, -1);

                                    intent.putExtra(Extras.EXTRA_STATS_PERIOD, date.getTimeInMillis());
                                }

                                afterTmp = new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivityForResult(intent, RequestCodes.REQUEST_STATS);
                                    }
                                };
                            }
                        }
                        break;

                        case "summary-show":
                        case "summary-show-period": {
                            String period = response.getResult().getParameters().containsKey("date-period") ? response.getResult().getParameters().get("date-period").getAsString() : null;

                            if (action.equals("summary-show") || (action.equals("summary-show-period") && period != null)) {
                                final Intent intent = new Intent(SceneActivity.this, SummaryActivity.class);

                                if (period != null) {
                                    String[] dates = period.split("/");
                                    final Calendar dateStart = Common.GetCalendarDateFromISO(dates[0]);
                                    final Calendar dateEnd = Common.GetCalendarDateFromISO(dates[1]);
                                    intent.putExtra(Extras.EXTRA_PERIOD_START, dateStart);
                                    intent.putExtra(Extras.EXTRA_PERIOD_END, dateEnd);
                                }

                                afterTmp = new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivityForResult(intent, RequestCodes.REQUEST_SUMMARY);
                                    }
                                };
                            }
                        }
                        break;

                        case "tips": {
                            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    if (intent.getAction() != null && intent.getAction().equals(CoachingService.BROADCAST_ACTION)) {
                                        LocalBroadcastManager.getInstance(SceneActivity.this).unregisterReceiver(this);
                                        String message = intent.getStringExtra(CoachingService.BROADCAST_EXTRA_MESSAGE);
                                        if (message == null || message.trim().length() == 0)
                                            message = "Something went wrong, I'm sorry!";

                                        sendMessageToUser(message, null);
                                    }
                                }
                            };
                            LocalBroadcastManager.getInstance(SceneActivity.this).registerReceiver(broadcastReceiver, new IntentFilter(CoachingService.BROADCAST_ACTION));

                            Intent serviceIntent = new Intent(SceneActivity.this, CoachingService.class);
                            serviceIntent.putExtra(CoachingService.ACTION, CoachingService.ACTION_START);
                            serviceIntent.putExtra(CoachingService.RESPONSE, CoachingService.RESPONSE_BROADCAST);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                startForegroundService(serviceIntent);
                            else
                                startService(serviceIntent);
                        }
                        break;

                        case "water-record":
                        case "water-record-time":
                        case "water-record-date":
                        case "water-record-datetime": {
                            final Calendar date = response.getResult().getParameters().containsKey("date") ? Common.GetCalendarDateFromISO(response.getResult().getParameters().get("date").getAsString()) : null;
                            final Calendar time = response.getResult().getParameters().containsKey("time") ? Common.GetCalendarTimeFromISO(response.getResult().getParameters().get("time").getAsString()) : null;

                            if (action.equals("water-record") || (action.equals("water-record-time") && time != null) || (action.equals("water-record-date") && date != null) || (action.equals("water-record-datetime") && time != null && date != null)) {
                                final Calendar cal = Calendar.getInstance();

                                if (date != null) {
                                    cal.set(Calendar.YEAR, date.get(Calendar.YEAR));
                                    cal.set(Calendar.MONTH, date.get(Calendar.MONTH));
                                    cal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                                } else {
                                    cal.setTime(new Date());
                                }

                                if (time != null) {
                                    cal.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
                                    cal.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
                                } else {
                                    if (date != null) {
                                        cal.set(Calendar.HOUR_OF_DAY, 12);
                                        cal.set(Calendar.MINUTE, 30);
                                    }
                                }

                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MILLISECOND, 0);


                                final Intent intent = new Intent(SceneActivity.this, WaterCreationActivity.class);
                                intent.putExtra(Extras.EXTRA_CALENDAR, cal);

                                afterTmp = new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivityForResult(intent, RequestCodes.REQUEST_WATER_CREATION);
                                    }
                                };
                            }
                        }
                        break;
                    }

                    final Runnable after = afterTmp;
                    sendMessageToUser(response.getResult().getFulfillment().getSpeech(), new UnityController.SpeechStatusListener() {
                        @Override
                        public void onStart(String path) {

                        }

                        @Override
                        public void onEnd(String path) {
                            if (after != null)
                                after.run();
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            };
        }

        if (listener != null)
            mDialogFlow.query(message, listener);
    }
    //endregion

    //region message functions
    @Override
    protected void sendMessageToAvatar(String message) {
        super.sendMessageToAvatar(message);
        if (message.trim().length() > 0) {
            //addUserMessage(message);
            updateMessages();
            analyzeMessage(message);
        }
    }

    @Override
    protected void sendMessageToUser(String message, UnityController.SpeechStatusListener listener) {
        super.sendMessageToUser(message, listener);
        if (message.trim().length() > 0) {
            //addAvatarMessage(message);
            updateMessages();
        }
    }

    private void addAvatarMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = new TextView(new ContextThemeWrapper(SceneActivity.this, R.style.SceneTextReceived), null, 0);
                textView.setText(message);
                mBinding.messageContainer.addView(textView);
                scrollMessageToBottom();
            }
        });

    }

    private void addUserMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = new TextView(new ContextThemeWrapper(SceneActivity.this, R.style.SceneTextSent), null, 0);
                textView.setText(message);
                mBinding.messageContainer.addView(textView);
                scrollMessageToBottom();
            }
        });

    }

    private void scrollMessageToBottom() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.messageScroll.post(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.messageScroll.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
    }
    //endregion

    @Override
    public void setUnitySettings() {
        super.setUnitySettings();
        ((UHealthApplication) getApplication()).getUnityController().setCamera(180, 1f, 1f);
        ((UHealthApplication) getApplication()).getUnityController().setCameraTarget(UnityController.CameraTarget.UPPER_BODY);

        updateMessages();
    }

    private void updateMessages() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.messageContainer.removeAllViews();
                ArrayList<UHealthApplication.ChatMessage> messages = (ArrayList<UHealthApplication.ChatMessage>) getMessages().clone();
                for (UHealthApplication.ChatMessage message : messages) {
                    switch (message.getSender()) {
                        case AVATAR:
                            addAvatarMessage(message.getMessage());
                            break;
                        case USER:
                            addUserMessage(message.getMessage());
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onUnityLoaded() {
        super.onUnityLoaded();
        showSystemUi();
        setGradientVisibility(true, true);
        if (!mFirstRun)
            setMenuVisibility(true, true);
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onAvatarLoading() {
        super.onAvatarLoading();
        setContentVisibility(false, true);
        setLoadingVisibility(true, true);
    }

    @Override
    public void onAvatarLoaded() {
        super.onAvatarLoaded();
        setContentVisibility(true, true);
        setLoadingVisibility(false, true);
        updateMessages();
    }

    private class UnitySpeechRecognitionListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (data != null && data.size() > 0) {
                mBinding.txtVoiceInput.setText(String.valueOf(data.get(0)));
                switchToTextInput(String.valueOf(data.get(0)));
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            ArrayList data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (data != null && data.size() > 0) {
                mBinding.txtVoiceInput.setText(String.valueOf(data.get(0)));
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }

    private interface CardStatusListener {
        void onShow();

        void onHide();
    }

}

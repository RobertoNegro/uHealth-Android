package com.negroroberto.uhealth.activities.abstracts;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.unity.UnityController;
import com.negroroberto.uhealth.utils.Extras;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;

import cn.dreamtobe.messagehandler.MessageHandler;

public abstract class UnityActivity extends UHealthActivity  implements UnityController.EventListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setUnity(FrameLayout frameLayout) {
        ((UHealthApplication)getApplication()).setUnityOnView(this, frameLayout);
       ((UHealthApplication)getApplication()).setUnityEventListener(this);

        setUnitySettings();
    }

    protected ArrayList<UHealthApplication.ChatMessage> getMessages() {
        return ((UHealthApplication)getApplication()).getMessages();
    }


    protected void quitUnity() {
        if(getUnityPlayer() != null)
            getUnityPlayer().quit();
    }

    protected UnityPlayer getUnityPlayer() {
        return ((UHealthApplication)getApplication()).getUnityInstance();
    }

    protected void showSystemUi() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                int immersiveFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                if (getUnityPlayer() != null)
                    getUnityPlayer().setSystemUiVisibility(getUnityPlayer().getSystemUiVisibility() & ~immersiveFlags);
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & ~immersiveFlags);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    protected void sendMessageToUser(String message, UnityController.SpeechStatusListener listener) {
        if (message.trim().length() > 0)
            ((UHealthApplication) getApplication()).avatarSpeech(message, listener);
    }
    protected void sendMessageToAvatar(String message) {
        if (message.trim().length() > 0)
            ((UHealthApplication) getApplication()).userSpeech(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((UHealthApplication)getApplication()).unityActivityPause();

        if (getUnityPlayer() != null)
            getUnityPlayer().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUnity((FrameLayout)findViewById(R.id.idUnity));
        ((UHealthApplication)getApplication()).unityActivityResume();

        if (getUnityPlayer() != null)
            getUnityPlayer().resume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getUnityPlayer() != null)
            getUnityPlayer().start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getUnityPlayer() != null)
            getUnityPlayer().stop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (getUnityPlayer() != null)
            getUnityPlayer().lowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (getUnityPlayer() != null && level == TRIM_MEMORY_RUNNING_CRITICAL)
            getUnityPlayer().lowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getUnityPlayer() != null)
            getUnityPlayer().configurationChanged(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (getUnityPlayer() != null)
            getUnityPlayer().windowFocusChanged(hasFocus);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (getUnityPlayer() != null && event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return getUnityPlayer().injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeActivity();
            return true;
        }
        if (getUnityPlayer() != null)
            return getUnityPlayer().injectEvent(event);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getUnityPlayer() != null)
            return getUnityPlayer().injectEvent(event);
        else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getUnityPlayer() != null)
            return getUnityPlayer().injectEvent(event);
        else
            return super.onTouchEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (getUnityPlayer() != null)
            return getUnityPlayer().injectEvent(event);
        else
            return super.onGenericMotionEvent(event);
    }

    @Override
    public void onUnityLoaded() {
        setUnitySettings();
    }

    @Override
    public void onAvatarLoading() {

    }

    @Override
    public void onAvatarLoaded() {

    }

    public void setUnitySettings() {

    }
}

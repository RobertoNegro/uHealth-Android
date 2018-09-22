package com.negroroberto.uhealth;

import android.app.Activity;
import android.app.Application;
import android.arch.persistence.room.Room;
import android.widget.FrameLayout;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.negroroberto.uhealth.db.UHealthDatabase;
import com.negroroberto.uhealth.unity.UnityController;
import com.negroroberto.uhealth.utils.Goals;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.Date;

import cn.dreamtobe.messagehandler.MessageHandler;


public class UHealthApplication extends Application implements UnityController.EventListener {
    private UHealthDatabase mUHealthDatabase;
    private Goals mGoals;
    private MessageHandler mHandler;

    private UnityController mUnityController;
    private UnityPlayer mUnityPlayer;
    private UnityController.EventListener mUnityEventListener;

    private ArrayList<RetardedMessage> mRetardedMessages;
    private ArrayList<ChatMessage> mMessages;

    private enum UnityState {
        UNITY_LOADING, UNITY_LOADED, AVATAR_LOADING, AVATAR_LOADED
    }

    private UnityState mUnityState;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        mUnityController = new UnityController(this);
        mUnityController.setEventListener(this);

        mHandler = new MessageHandler();

        mUnityState = UnityState.UNITY_LOADING;

        mRetardedMessages = new ArrayList<>();
        mMessages = new ArrayList<>();
    }

    @Override
    public void onUnityLoaded() {
        mUnityState = UnityState.UNITY_LOADED;

        mUnityController.setBackground("#00695b");
        mUnityController.createAvatar(UnityController.AvatarSex.FEMALE, UnityController.AvatarBody.MEDIC, "b7ce00b9-3589-4dbd-9b5b-2466233e9d89", "063f8e92-4675-4bee-8f4f-a303779b4e3c", "female_NewSea_J123f", "#3E271F");

        mUnityEventListener.onUnityLoaded();
    }

    @Override
    public void onAvatarLoading() {
        mUnityState = UnityState.AVATAR_LOADING;

        mUnityEventListener.onAvatarLoading();
    }

    @Override
    public void onAvatarLoaded() {
        mUnityState = UnityState.AVATAR_LOADED;

        String avatarMessage;
        if (mRetardedMessages.size() > 0) {
            for (RetardedMessage message : mRetardedMessages)
                avatarSpeech(message.getMessage(), message.getSpeechStatusListener());
            mRetardedMessages.clear();
        } else {
            avatarMessage = "Hello! I'm Alice, your personal assistant. How can I help you?";
            avatarSpeech(avatarMessage, new UnityController.SpeechStatusListener() {
                @Override
                public void onStart(String path) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mUnityController.animGreetings();
                        }
                    }, 200);
                }

                @Override
                public void onEnd(String path) {

                }

                @Override
                public void onError() {

                }
            });
        }

        mUnityEventListener.onAvatarLoaded();
    }

    public void avatarSpeech(String message, UnityController.SpeechStatusListener listener) {
        if (message != null && message.trim().length() > 0) {
            if (mUnityState == UnityState.AVATAR_LOADED) {
                mUnityController.speech(message, listener);
                mMessages.add(new ChatMessage(Sender.AVATAR, message, new Date().getTime()));
            } else
                mRetardedMessages.add(new RetardedMessage(message, listener));
        } else
            listener.onError();
    }

    public void userSpeech(String message) {
        if (message != null && message.trim().length() > 0) {
            mMessages.add(new ChatMessage(Sender.USER, message, new Date().getTime()));
        }
    }

    public UHealthDatabase getDatabaseInstance() {
        if (mUHealthDatabase == null)
            mUHealthDatabase = Room.databaseBuilder(this, UHealthDatabase.class, UHealthDatabase.NAME).build();

        return mUHealthDatabase;
    }

    public Goals getGoals() {
        if (mGoals == null)
            mGoals = new Goals(this);

        return mGoals;
    }

    public void setUnityOnView(final Activity activity, final FrameLayout unityContainer) {
        if (mUnityPlayer == null)
            mUnityPlayer = new UnityPlayer(activity);

        if(mUnityEventListener != null) {
            if (mUnityState == UnityState.UNITY_LOADED || mUnityState == UnityState.AVATAR_LOADING || mUnityState == UnityState.AVATAR_LOADED)
                mUnityEventListener.onUnityLoaded();
            if(mUnityState == UnityState.AVATAR_LOADING || mUnityState == UnityState.AVATAR_LOADED)
                mUnityEventListener.onAvatarLoading();
            if(mUnityState == UnityState.AVATAR_LOADED)
                mUnityEventListener.onAvatarLoaded();
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mUnityPlayer.getView().getParent() != null)
                    ((FrameLayout) mUnityPlayer.getView().getParent()).removeView(mUnityPlayer.getView());

                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                unityContainer.addView(mUnityPlayer.getView(), 0, layoutParams);
                mUnityPlayer.requestFocus();
            }
        });

    }

    public void setUnityEventListener(UnityController.EventListener listener) {
        mUnityEventListener = listener;
    }


    public UnityController getUnityController() {
        return mUnityController;
    }

    public UnityPlayer getUnityInstance() {
        return mUnityPlayer;
    }

    public void unityActivityPause() {
        mHandler.pause();
    }

    public void unityActivityResume() {
        mHandler.resume();
    }

    public enum Sender {
        AVATAR, USER
    }

    public ArrayList<ChatMessage> getMessages() {
        return mMessages;
    }

    private class RetardedMessage {
        private UnityController.SpeechStatusListener mSpeechStatusListener;
        private String mMessage;

        public RetardedMessage( String message, UnityController.SpeechStatusListener speechStatusListener) {
            mSpeechStatusListener = speechStatusListener;
            mMessage = message;
        }

        public UnityController.SpeechStatusListener getSpeechStatusListener() {
            return mSpeechStatusListener;
        }

        public void setSpeechStatusListener(UnityController.SpeechStatusListener speechStatusListener) {
            mSpeechStatusListener = speechStatusListener;
        }

        public String getMessage() {
            return mMessage;
        }

        public void setMessage(String message) {
            mMessage = message;
        }
    }
    public class ChatMessage {
        private Sender mSender;
        private String mMessage;
        private long mTime;

        public ChatMessage(Sender sender, String message, long time) {
            mSender = sender;
            mMessage = message;
            mTime = time;
        }

        public Sender getSender() {
            return mSender;
        }

        public void setSender(Sender sender) {
            mSender = sender;
        }

        public String getMessage() {
            return mMessage;
        }

        public void setMessage(String message) {
            mMessage = message;
        }

        public long getTime() {
            return mTime;
        }

        public void setTime(long time) {
            mTime = time;
        }
    }
}

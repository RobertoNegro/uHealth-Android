package com.negroroberto.uhealth.unity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import com.negroroberto.uhealth.tts.PollyTTS;
import com.negroroberto.uhealth.utils.Debug;

import java.net.URL;
import java.util.HashMap;

public class UnityController implements AppSocket.OnReceiveListener {
    private Context mContext;
    private PollyTTS mPollyTTS;
    private HashMap<String, SpeechStatusListener> mAfterSpeech = new HashMap<>();
    private EventListener mEventListener;

    public enum AvatarSex {
        MALE, FEMALE
    }

    public enum AvatarBody {
        SPORT_M, SPORT_L, SPORT_XL, SPORT_XXL, SPORT_XXXL, MEDIC
    }

    public interface EventListener {
        void onUnityLoaded();
        void onAvatarLoading();
        void onAvatarLoaded();
    }

    public interface SpeechStatusListener {
        void onStart(String path);
        void onEnd(String path);
        void onError();
    }

    public UnityController(@NonNull Context context) {
        mContext = context;
        mPollyTTS = new PollyTTS(context);
    }

    public void setEventListener(EventListener listener) {
        AppSocket.SetOnReceiveListener(this);
        mEventListener = listener;
    }


    public void createAvatar(AvatarSex sex, AvatarBody body, String playerId, String avatarId, String hairId, String hairColor) {
        String bodySex = "";
        switch (sex){
            case MALE:
                bodySex = "male";
                break;
            case FEMALE:
                bodySex = "female";
                break;
        }

        StringBuilder bodyName = new StringBuilder();

        switch (body) {
            case MEDIC:
                bodyName.append("medic").append('_').append(bodySex);
                break;
            case SPORT_M:
                bodyName.append("sport").append('_').append(bodySex).append('_').append("m");
                break;
            case SPORT_L:
                bodyName.append("sport").append('_').append(bodySex).append('_').append("l");
                break;
            case SPORT_XL:
                bodyName.append("sport").append('_').append(bodySex).append('_').append("xl");
                break;
            case SPORT_XXL:
                bodyName.append("sport").append('_').append(bodySex).append('_').append("xxl");
                break;
            case SPORT_XXXL:
                bodyName.append("sport").append('_').append(bodySex).append('_').append("xxxl");
                break;
        }

        AppSocket.SendCommand(AppSocket.SendType.AVATAR_CREATE, playerId, bodyName.toString(), avatarId, hairId, hairColor);
    }

    public void animGreetings() {
        AppSocket.SendCommand(AppSocket.SendType.ANIM_GREETINGS);
    }

    public void animRunStart() {
        AppSocket.SendCommand(AppSocket.SendType.ANIM_RUN_TRUE);
    }

    public void animRunStop() {
        AppSocket.SendCommand(AppSocket.SendType.ANIM_RUN_FALSE);
    }

    public void speech(String text, final SpeechStatusListener listener) {
        mPollyTTS.speech(text, new PollyTTS.OnSpeechReadyListener() {
            @Override
            public void onSpeechReady(URL url) {
                if(url != null) {
                    AppSocket.SendCommand(AppSocket.SendType.SPEECH, url.toString());
                    mAfterSpeech.put(url.toString(), listener);
                } else {
                    if (listener != null)
                        listener.onError();
                }
            }
        });
    }

    public void shutup() {
        AppSocket.SendCommand(AppSocket.SendType.SPEECH, "");
    }

    public void setCamera(Integer angle, Float distance, Float height) {
        if(angle != null)
            AppSocket.SendCommand(AppSocket.SendType.CAMERA_ANGLE, String.valueOf(angle));
        if(distance != null)
            AppSocket.SendCommand(AppSocket.SendType.CAMERA_DISTANCE, String.valueOf(distance));
        if(height != null)
            AppSocket.SendCommand(AppSocket.SendType.CAMERA_HEIGHT, String.valueOf(height));
    }

    public enum CameraTarget {
        HEAD, UPPER_BODY, HALF_BODY
    }
    public void setCameraTarget(CameraTarget target) {
        switch (target) {
            case HEAD:
                AppSocket.SendCommand(AppSocket.SendType.CAMERA_TARGET_HEAD);
                break;
            case UPPER_BODY:
                AppSocket.SendCommand(AppSocket.SendType.CAMERA_TARGET_UPPER_BODY);
                break;
            case HALF_BODY:
                AppSocket.SendCommand(AppSocket.SendType.CAMERA_TARGET_HALF_BODY);
                break;
        }
    }

    public void setBackground(String color) {
        AppSocket.SendCommand(AppSocket.SendType.BG_COLOR, color);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onReceive(AppSocket.ReceiveType receiveType, String... parameters) {
        Debug.Log(this,"Received command: " + receiveType.name());

        switch (receiveType) {
            case UNITY_LOADED:
                if(mEventListener != null)
                    mEventListener.onUnityLoaded();
                break;
            case AVATAR_LOADING:
                if(mEventListener != null)
                    mEventListener.onAvatarLoading();
                break;
            case AVATAR_FINISH:
                if(mEventListener != null)
                    mEventListener.onAvatarLoaded();
                break;
            case SPEECH_START:
                if (mAfterSpeech.containsKey(parameters[0])) {
                    SpeechStatusListener listener = (mAfterSpeech.get(parameters[0]));
                    if (listener != null)
                        listener.onStart(parameters[0]);
                }
                break;
            case SPEECH_FINISH:
                if (mAfterSpeech.containsKey(parameters[0])) {
                    SpeechStatusListener listener = (mAfterSpeech.remove(parameters[0]));
                    if(listener != null)
                        listener.onEnd(parameters[0]);
                }
                break;
        }
    }
}

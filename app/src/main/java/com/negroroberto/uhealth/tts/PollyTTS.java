package com.negroroberto.uhealth.tts;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.LanguageCode;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;
import com.negroroberto.uhealth.utils.Debug;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PollyTTS {
    private static final String COGNITO_POOL_ID ="eu-central-1:f1a3b579-2ed4-4d47-9c7a-88469807d882";
    private static final Regions MY_REGION = Regions.EU_CENTRAL_1;

    private Context mContext;

    private AmazonPollyPresigningClient mClient;
    private String mVoiceId;

    public PollyTTS(Context context) {
        this(context, "Joanna");
    }
    public PollyTTS(Context context, String voiceId) {
        mContext = context;
        mVoiceId = voiceId;
        init();
    }

    private void init() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                mContext,
                COGNITO_POOL_ID,
                MY_REGION
        );
        mClient = new AmazonPollyPresigningClient(credentialsProvider);
    }

    public void getVoicesList(OnVoicesReadyListener listener) {
        getVoicesList(null, listener);
    }
    public void getVoicesList(LanguageCode languageCode, OnVoicesReadyListener listener) {
        GetVoicesTask task = new GetVoicesTask(mClient, languageCode,listener);
        task.execute();
    }

    public Context getContext() {
        return mContext;
    }
    public void setContext(@NotNull Context context) {
        mContext = context;
    }

    public String getVoiceId() {
        return mVoiceId;
    }
    public void setVoiceId(@NotNull String voiceId) {
        mVoiceId = voiceId;
    }
    public void setVoice(@NotNull Voice voice) {
        setVoiceId(voice.getId());
    }

    public void speech(String text, OnSpeechReadyListener listener) {
        SpeechTask task = new SpeechTask(mClient, mVoiceId, text, listener);
        task.execute();
    }

    public interface OnVoicesReadyListener {
        void onVoicesReady(List<Voice> voices);
    }
    private static class GetVoicesTask extends  AsyncTask<Void, Void, Void> {
        private AmazonPollyPresigningClient mClient;
        private LanguageCode mLanguageCode;
        private OnVoicesReadyListener mListener;

        public GetVoicesTask(@NotNull AmazonPollyPresigningClient client, LanguageCode languageCode, OnVoicesReadyListener listener) {
            mClient = client;
            mLanguageCode = languageCode;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();
            if(mLanguageCode != null)
                describeVoicesRequest.setLanguageCode(mLanguageCode);
            DescribeVoicesResult describeVoicesResult = mClient.describeVoices(describeVoicesRequest);

            if(mListener != null)
                mListener.onVoicesReady(describeVoicesResult.getVoices());

            return null;
        }
    }

    public interface OnSpeechReadyListener {
        void onSpeechReady(URL url);
    }
    private static class SpeechTask extends AsyncTask<Void, Void, Void> {
        private AmazonPollyPresigningClient mClient;
        private String mVoiceId;
        private String mText;
        private OnSpeechReadyListener mListener;

        public SpeechTask(@NotNull AmazonPollyPresigningClient client, @NotNull String voiceId, @NotNull String text, OnSpeechReadyListener listener) {
            mClient = client;
            mVoiceId = voiceId;
            mText = text;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest = new SynthesizeSpeechPresignRequest().withText(mText).withVoiceId(mVoiceId).withOutputFormat(OutputFormat.Mp3);
            if(mListener != null)
                mListener.onSpeechReady(mClient.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest));
            return null;
        }
    }

    public void playAudio(URL url) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url.toString());
        } catch (IOException e) {
            Debug.Err(this, "Unable to set data source for the media player! " + e.getMessage());
        }

        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }
}

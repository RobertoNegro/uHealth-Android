package com.negroroberto.uhealth.ai;

import android.os.AsyncTask;

import com.negroroberto.uhealth.utils.Debug;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class DialogFlow {
    private final static String ACCESS_TOKEN = "92e849d241be4f88ac5d5a4009cb235a";

    private AIDataService mAiDataService;

    public DialogFlow() {
        final AIConfiguration config = new AIConfiguration(ACCESS_TOKEN, AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
        mAiDataService = new AIDataService(config);
    }

    public interface OnResponseListener {
        void onResponse(AIResponse response);
    }

    public void query(String text, OnResponseListener listener) {
        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(text);

        DialogRequestTask task = new DialogRequestTask(mAiDataService, aiRequest, listener);
        task.execute();
    }

    private static class DialogRequestTask extends AsyncTask<Void, Void, AIResponse> {
        private AIDataService mAiDataService;
        private AIRequest mRequest;
        private OnResponseListener mListener;

        public DialogRequestTask(AIDataService aiDataService, AIRequest request, OnResponseListener listener) {
            mAiDataService = aiDataService;
            mRequest = request;
            mListener = listener;
        }

        @Override
        protected AIResponse doInBackground(Void... voids) {
            try {
                return mAiDataService.request(mRequest);
            } catch (AIServiceException e) {
                Debug.Err(this, "Error sending request to DialogFlow: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(AIResponse aiResponse) {
            mListener.onResponse(aiResponse);
        }
    }
}

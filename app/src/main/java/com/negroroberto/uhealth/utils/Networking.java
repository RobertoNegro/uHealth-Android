package com.negroroberto.uhealth.utils;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Networking {
    private static OkHttpClient client = new OkHttpClient();

    public static void Get(String url, ResponseListener listener) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        doRequest(request, listener);
    }


    private static void doRequest(Request request, ResponseListener listener) {
        RequestTask task = new RequestTask(listener);
        task.execute(request);
    }

    public interface ResponseListener {
        void onResponse(Response response);
    }

    private static class RequestTask extends AsyncTask<Request, Void, Void> {
        private ResponseListener mListener;

        protected RequestTask(ResponseListener listener) {
            mListener = listener;

            if(mListener == null)
                Debug.Warn(RequestTask.this, "Null response listener");
        }

        @Override
        protected Void doInBackground(Request... requests) {
            for (Request r : requests) {
                try (Response response = client.newCall(r).execute()) {
                    if(mListener != null)
                        mListener.onResponse(response);
                } catch (IOException e) {
                    if(mListener != null)
                        mListener.onResponse(null);
                }
            }
            return null;
        }
    }

    public static boolean IsSuccessful(Response response) {
        return (response != null && response.isSuccessful());
    }

    public static boolean HasEmptyBody(Response response) {
        ResponseBody body = response.body();
        return (body == null || body.contentLength() == 0);
    }
}

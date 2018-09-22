package com.negroroberto.uhealth.modules.sport;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.modules.Module;
import com.negroroberto.uhealth.utils.Debug;

import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

public class GoogleFitModule extends Module {
    private GoogleSignInAccount mAccount;

    public GoogleFitModule(UHealthApplication application, GoogleSignInAccount account) {
        super(application);
        mAccount = account;
    }

    public void getDuration(final long startTime, final long endTime, final OnLongResultListener listener) {
        DataSource activitySegmentSource = new DataSource.Builder()
                .setAppPackageName("com.google.android.gms")
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_activity_segment")
                .build();

        DataReadRequest durationRequest = new DataReadRequest.Builder()
                .aggregate(activitySegmentSource, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .bucketByActivitySegment(1, TimeUnit.SECONDS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        getFitData(durationRequest, new OnDataAvailableListener() {
            @Override
            public void onDataAvailable(DataReadResponse response) {
                long totalTime = 0;
                for (Bucket b : response.getBuckets()) {
                    if (!b.getActivity().equals("still") && !b.getActivity().equals("in_vehicle") && !b.getActivity().equals("unknown"))
                        totalTime += (b.getEndTime(TimeUnit.MILLISECONDS) - b.getStartTime(TimeUnit.MILLISECONDS));
                }

                listener.onResult(totalTime / 1000 / 60);
            }
        });
    }

    public void getDistance(final long startTime, final long endTime, final OnFloatResultListener listener){

        DataReadRequest distanceRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        getFitData(distanceRequest, new OnDataAvailableListener() {
            @Override
            public void onDataAvailable(DataReadResponse response) {
                float result = 0;
                for(Bucket b : response.getBuckets()) {
                    for (DataSet ds : b.getDataSets()) {
                        for(DataPoint dp : ds.getDataPoints()) {
                            if(dp.getDataType().getFields().size() > 0) {
                                Field field = dp.getDataType().getFields().get(0);
                                result += dp.getValue(field).asFloat();
                            }
                        }
                    }
                }
                listener.onResult(result);
            }
        });

    }

    public void getCalories(final long startTime, final long endTime, final OnFloatResultListener listener) {
        DataReadRequest caloriesRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByActivityType(1, TimeUnit.SECONDS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        getFitData(caloriesRequest, new OnDataAvailableListener() {
            @Override
            public void onDataAvailable(DataReadResponse response) {
                float result = 0;

                for (Bucket b : response.getBuckets()) {
                    if (!b.getActivity().equals("still") && !b.getActivity().equals("in_vehicle") && !b.getActivity().equals("unknown")) {
                        for (DataSet ds : b.getDataSets()) {
                            for(DataPoint dp : ds.getDataPoints()) {
                                if(dp.getDataType().getFields().size() > 0) {
                                    Field field = dp.getDataType().getFields().get(0);
                                    result += dp.getValue(field).asFloat();
                                }
                            }
                        }
                    }
                }
                listener.onResult(result);
            }
        });
    }

    public void getSteps(final long startTime, final long endTime, final OnLongResultListener listener){
        DataSource stepSource = new DataSource.Builder()
                .setAppPackageName("com.google.android.gms")
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .build();

        DataReadRequest stepRequest = new DataReadRequest.Builder()
                .aggregate(stepSource, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        getFitData(stepRequest, new OnDataAvailableListener() {
            @Override
            public void onDataAvailable(DataReadResponse response) {
                int result = 0;

                for(Bucket b : response.getBuckets()) {
                    for (DataSet ds : b.getDataSets()) {
                        for(DataPoint dp : ds.getDataPoints()) {
                            if(dp.getDataType().getFields().size() > 0) {
                                Field field = dp.getDataType().getFields().get(0);
                                result += dp.getValue(field).asInt();
                            }
                        }
                    }
                }
                listener.onResult(result);
            }
        });

    }

    private interface OnDataAvailableListener {
        void onDataAvailable(DataReadResponse response);
    }

    private void getFitData( DataReadRequest readRequest, final OnDataAvailableListener listener) {
        Fitness.getHistoryClient(getApplication(), mAccount)
                .readData(readRequest)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Debug.Err(this, "Failure on getting fit data! Error: " + e.toString());
                        listener.onDataAvailable(null);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        listener.onDataAvailable(dataReadResponse);
                    }
                });
    }

    private void debugDataSet(DataSet dataSet) {
        Debug.Log(this, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat df = DateFormat.getDateTimeInstance();
        for (DataPoint dp : dataSet.getDataPoints()) {
            Debug.Log(this, "Data point: { Type=" + dp.getDataType().getName() + "; Start=" + df.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + "; END=" + df.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " }");
            for (Field field : dp.getDataType().getFields()) {
                Debug.Log(this, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
            }
        }
    }

    public interface OnFloatResultListener {
        void onResult(float result);
    }
    public interface OnLongResultListener {
        void onResult(long result);
    }
}

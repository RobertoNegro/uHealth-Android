package com.negroroberto.uhealth.modules.water;

import android.content.Context;
import android.os.AsyncTask;

import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.modules.Module;
import com.negroroberto.uhealth.modules.water.models.WaterDrunk;

import java.util.ArrayList;

public class WaterModule extends Module {
    public WaterModule(UHealthApplication application) {
        super(application);
    }

    public void removeWater(final OnVoidResultListener listener, final WaterDrunk waterDrunk) {
        DeleteWaterTask task = new DeleteWaterTask(getApplication(), listener);
        task.execute(waterDrunk);
    }

    public void updateWater(final OnVoidResultListener listener, final WaterDrunk waterDrunk) {
        UpdateWaterTask task = new UpdateWaterTask(getApplication(), listener);
        task.execute(waterDrunk);
    }

    public void createWater(final OnLongResultListener listener, final WaterDrunk waterDrunk) {
        InsertWaterTask task = new InsertWaterTask(getApplication(), new OnLongArrayResultListener() {
            @Override
            public void onResult(long[] result) {
                if (result.length > 0)
                    listener.onResult(result[0]);
            }
        });
        task.execute(waterDrunk);
    }

    public void getWaterByPeriod(final long startTime, final long endTime, final OnWaterDrunkListResultListener listener) {
        GetWaterByPeriodTask task = new GetWaterByPeriodTask(getApplication(), startTime, endTime, listener);
        task.execute();
    }

    private static class InsertWaterTask extends AsyncTask<WaterDrunk, Void, Void> {
        private OnLongArrayResultListener mListener;
        private UHealthApplication mApplication;

        public InsertWaterTask(UHealthApplication application,OnLongArrayResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(WaterDrunk... waterDrunks) {
            long[] ids = mApplication.getDatabaseInstance().waterDrunkDao().insertAll(waterDrunks);

            if (mListener != null)
                mListener.onResult(ids);
            return null;
        }
    }

    private static class DeleteWaterTask extends AsyncTask<WaterDrunk, Void, Void> {
        private OnVoidResultListener mListener;
        private UHealthApplication mApplication;

        public DeleteWaterTask(UHealthApplication application,OnVoidResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(WaterDrunk... waterDrunksals) {
            for (WaterDrunk w : waterDrunksals)
                mApplication.getDatabaseInstance().waterDrunkDao().delete(w);

            if (mListener != null)
                mListener.onResult();
            return null;
        }
    }

    private static class UpdateWaterTask extends AsyncTask<WaterDrunk, Void, Void> {
        private OnVoidResultListener mListener;
        private UHealthApplication mApplication;

        public UpdateWaterTask(UHealthApplication application,OnVoidResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(WaterDrunk... waterDrunks) {
            for (WaterDrunk w : waterDrunks)
                mApplication.getDatabaseInstance().waterDrunkDao().update(w);

            if (mListener != null)
                mListener.onResult();
            return null;
        }
    }

    private static class GetWaterByIdTask extends AsyncTask<Long, Void, Void> {
        private OnWaterDrunkListResultListener mListener;
        private UHealthApplication mApplication;

        public GetWaterByIdTask(UHealthApplication application,OnWaterDrunkListResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Long... ids) {
            ArrayList<WaterDrunk> waterDrunks = new ArrayList<>();
            for(Long id : ids)
                waterDrunks.add(mApplication.getDatabaseInstance().waterDrunkDao().findById(id));

            if (mListener != null)
                mListener.onResult(waterDrunks);
            return null;
        }
    }

    private static class GetWaterByPeriodTask extends AsyncTask<Void, Void, Void> {
        private long mStartTime;
        private long mEndTime;
        private OnWaterDrunkListResultListener mListener;
        private UHealthApplication mApplication;

        public GetWaterByPeriodTask(UHealthApplication application,long startTime, long endTime, OnWaterDrunkListResultListener listener) {
            mApplication = application;
            mStartTime = startTime;
            mEndTime = endTime;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<WaterDrunk> waterDrunks = new ArrayList<>(mApplication.getDatabaseInstance().waterDrunkDao().findByPeriod(mStartTime, mEndTime));
            if (mListener != null)
                mListener.onResult(waterDrunks);
            return null;
        }
    }

    public interface OnVoidResultListener {
        void onResult();
    }
    public interface OnLongResultListener {
        void onResult(long result);
    }
    public interface OnLongArrayResultListener {
        void onResult(long[] result);
    }
    public interface OnWaterDrunkListResultListener {
        void onResult(ArrayList<WaterDrunk> result);
    }
}

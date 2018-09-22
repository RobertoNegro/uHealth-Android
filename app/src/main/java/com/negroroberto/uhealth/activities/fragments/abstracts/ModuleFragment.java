package com.negroroberto.uhealth.activities.fragments.abstracts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.negroroberto.uhealth.utils.Debug;

import java.util.Calendar;
import java.util.Date;

public abstract class ModuleFragment extends Fragment {
    private static final String ARGS_TIME_START = "ARGS_TIME_START";
    private static final String ARGS_TIME_END = "ARGS_TIME_END";

    private long mTimeStart;
    private long mTimeEnd;

    public static ModuleFragment newInstance(Class fragmentClass, Calendar start, Calendar end) {
        ModuleFragment fragment = null;

        try {
            fragment = (ModuleFragment)fragmentClass.newInstance();
        } catch (java.lang.InstantiationException e) {
            Debug.Err(null, "Instantiation error: " + e.toString());
        } catch (IllegalAccessException e) {
            Debug.Err(null, "Illegal access error: " + e.toString());
        } catch (ClassCastException e) {
            Debug.Err(null, "Invalid cast class: " + e.toString());
        }

        if(fragment != null) {
            Bundle args = new Bundle();
            args.putLong(ARGS_TIME_START, start.getTimeInMillis());
            args.putLong(ARGS_TIME_END, end.getTimeInMillis());

            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTimeStart = getArguments().getLong(ARGS_TIME_START, 0);
            mTimeEnd = getArguments().getLong(ARGS_TIME_END, 0);
        } else {
            mTimeStart = 0;
            mTimeEnd = 0;
        }
    }

    public void setDay(long day) {
        Calendar start = Calendar.getInstance();
        start.setTime(new Date(day));
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = (Calendar)start.clone();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
    }

    public void setPeriod(long start, long end) {
        mTimeStart = start;
        mTimeEnd = end;
        updateData();
    }

    public long getTimeStart() {
        return mTimeStart;
    }

    public long getTimeEnd() {
        Calendar actual = Calendar.getInstance();
        actual.setTime(new Date());
        long actMillis = actual.getTimeInMillis();

        if (actMillis < mTimeEnd)
            return actMillis;
        else
            return mTimeEnd;
    }


    public abstract void updateData();
}

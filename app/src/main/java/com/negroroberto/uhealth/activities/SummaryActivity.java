package com.negroroberto.uhealth.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.abstracts.GoogleSignInListener;
import com.negroroberto.uhealth.activities.abstracts.UHealthActivity;
import com.negroroberto.uhealth.activities.abstracts.UnityActivity;
import com.negroroberto.uhealth.databinding.ActivitySummaryBinding;
import com.negroroberto.uhealth.modules.food.FoodModule;
import com.negroroberto.uhealth.modules.food.models.Meal;
import com.negroroberto.uhealth.modules.sport.GoogleFitModule;
import com.negroroberto.uhealth.modules.water.WaterModule;
import com.negroroberto.uhealth.modules.water.models.WaterDrunk;
import com.negroroberto.uhealth.unity.UnityController;
import com.negroroberto.uhealth.utils.Debug;
import com.negroroberto.uhealth.utils.Extras;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SummaryActivity extends UnityActivity {
    private ActivitySummaryBinding mBinding;
    private Calendar mPeriodStart;
    private Calendar mPeriodEnd;

    private FoodModule mFoodModule;
    private WaterModule mWaterModule;
    private GoogleFitModule mGoogleFitModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_summary);
        setResult(RESULT_CANCELED);

        mFoodModule = new FoodModule((UHealthApplication)getApplication());
        mWaterModule = new WaterModule((UHealthApplication)getApplication());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mPeriodStart = (Calendar) getIntent().getSerializableExtra(Extras.EXTRA_PERIOD_START);
        mPeriodEnd = (Calendar) getIntent().getSerializableExtra(Extras.EXTRA_PERIOD_END);

        if (mPeriodEnd == null) {
            mPeriodEnd = Calendar.getInstance();
            mPeriodEnd.setTime(new Date());
        }
        mPeriodEnd.set(Calendar.HOUR_OF_DAY, 23);
        mPeriodEnd.set(Calendar.MINUTE, 59);
        mPeriodEnd.set(Calendar.SECOND, 59);
        mPeriodEnd.set(Calendar.MILLISECOND, 999);

        if (mPeriodStart == null) {
            mPeriodStart = (Calendar) mPeriodEnd.clone();
            mPeriodStart.add(Calendar.DAY_OF_YEAR, -6);
        }
        mPeriodStart.set(Calendar.HOUR_OF_DAY, 0);
        mPeriodStart.set(Calendar.MINUTE, 0);
        mPeriodStart.set(Calendar.SECOND, 0);
        mPeriodStart.set(Calendar.MILLISECOND, 0);

        if (mPeriodStart.compareTo(mPeriodEnd) > 0) {
            Calendar calendar = mPeriodStart;
            mPeriodStart = mPeriodEnd;
            mPeriodEnd = calendar;
        }

        updateFoodGraph();
        mBinding.spinnerFood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFoodGraph();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        updateWaterGraph();
        mBinding.spinnerWater.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateWaterGraph();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signInWithGoogle(new GoogleSignInListener() {
            @Override
            public void onSignIn(GoogleSignInAccount account) {
                mGoogleFitModule = new GoogleFitModule(((UHealthApplication)getApplication()), account);

                updateSportGraph();
                mBinding.spinnerSport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        updateSportGraph();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        updateDateText();

        mBinding.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog startDatePickerDialog = new DatePickerDialog(SummaryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mPeriodStart.set(Calendar.YEAR, year);
                        mPeriodStart.set(Calendar.MONTH, month);
                        mPeriodStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        if(mPeriodEnd.before(mPeriodStart)) {
                            mPeriodEnd.set(Calendar.YEAR, year);
                            mPeriodEnd.set(Calendar.MONTH, month);
                            mPeriodEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth+1);
                        }

                        updateDateText();
                        updateFoodGraph();
                        updateSportGraph();
                        updateWaterGraph();

                        DatePickerDialog endDatePickerDialog = new DatePickerDialog(SummaryActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                mPeriodEnd.set(Calendar.YEAR, year);
                                mPeriodEnd.set(Calendar.MONTH, month);
                                mPeriodEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                if(mPeriodStart.after(mPeriodEnd)) {
                                    mPeriodStart.set(Calendar.YEAR, year);
                                    mPeriodStart.set(Calendar.MONTH, month);
                                    mPeriodStart.set(Calendar.DAY_OF_MONTH, dayOfMonth-1);
                                }

                                updateDateText();
                                updateFoodGraph();
                                updateSportGraph();
                                updateWaterGraph();
                            }
                        }, mPeriodEnd.get(Calendar.YEAR), mPeriodEnd.get(Calendar.MONTH), mPeriodEnd.get(Calendar.DAY_OF_MONTH));
                        endDatePickerDialog.show();
                    }
                }, mPeriodStart.get(Calendar.YEAR), mPeriodStart.get(Calendar.MONTH), mPeriodStart.get(Calendar.DAY_OF_MONTH));
                startDatePickerDialog.show();
            }
        });

        mBinding.idUnity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this, SceneActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void setUnitySettings() {
        super.setUnitySettings();

        ((UHealthApplication)getApplication()).getUnityController().setCameraTarget(UnityController.CameraTarget.HEAD);
        ((UHealthApplication)getApplication()).getUnityController().setCamera(180, 0.4f, 0.9f);
    }

    private boolean mFirstTime = true;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && mFirstTime) {
            mFirstTime = false;
            View parent = (View) mBinding.idUnity.getParent();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.idUnity.getLayoutParams();
            params.leftMargin = (int) parent.getWidth() - mBinding.idUnity.getWidth() - 30;
            params.topMargin = (int) parent.getHeight() - mBinding.idUnity.getHeight() - 30;
            mBinding.idUnity.setLayoutParams(params);
        }
    }


    private void updateDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        mBinding.txtFromDate.setText(sdf.format(mPeriodStart.getTime()));
        mBinding.txtToDate.setText(sdf.format(mPeriodEnd.getTime()));
    }

    private void updateFoodGraph() {
        String foodSpinnerText = mBinding.spinnerFood.getSelectedItem().toString();
        updateFoodGraph(foodSpinnerText, (Calendar) mPeriodStart.clone(), new ArrayList<Entry>(), 0, 0);
    }

    private void updateFoodGraph(final String category, final Calendar actDay, final ArrayList<Entry> points, final int i, final double maxY) {
        if (actDay.before(mPeriodEnd)) {
            final Calendar actDayStart = (Calendar) actDay.clone();
            actDayStart.set(Calendar.HOUR_OF_DAY, 0);
            actDayStart.set(Calendar.MINUTE, 0);
            actDayStart.set(Calendar.SECOND, 0);
            actDayStart.set(Calendar.MILLISECOND, 0);

            final Calendar actDayEnd = (Calendar) actDayStart.clone();
            actDayEnd.set(Calendar.HOUR_OF_DAY, 23);
            actDayEnd.set(Calendar.MINUTE, 59);
            actDayEnd.set(Calendar.SECOND, 59);
            actDayEnd.set(Calendar.MILLISECOND, 999);

            mFoodModule.getMealsByPeriod(actDay.getTimeInMillis(), actDayEnd.getTimeInMillis(), new FoodModule.OnMealsListResultListener() {
                @Override
                public void onResult(ArrayList<Meal> result) {
                    FoodModule.OnDoubleResultListener listener = new FoodModule.OnDoubleResultListener() {
                        @Override
                        public void onResult(double result) {
                            points.add(new Entry(actDayStart.getTimeInMillis(), (float) result));

                            actDay.add(Calendar.DAY_OF_YEAR, 1);
                            updateFoodGraph(category, actDay, points, i + 1, maxY > result ? maxY : result);
                        }
                    };

                    switch (category) {
                        case "Calories": {
                            mFoodModule.getMealListTotalCalories(result, listener);
                        }
                        break;
                        case "Carbohydrates": {
                            mFoodModule.getMealListTotalCarbs(result, listener);
                        }
                        break;
                        case "Protein": {
                            mFoodModule.getMealListTotalProtein(result, listener);
                        }
                        break;
                        case "Fat": {
                            mFoodModule.getMealListTotalFat(result, listener);
                        }
                        break;
                        default: {
                            updateFoodGraph(category, actDay, points, i + 1, maxY);
                        }
                        break;
                    }
                }
            });
        } else {
            final float goal;

            switch (category) {
                case "Calories": {
                    goal = ((UHealthApplication)getApplication()).getGoals().getFoodCalories();
                }
                break;
                case "Carbohydrates": {
                    goal = ((UHealthApplication)getApplication()).getGoals().getFoodCarbs();
                }
                break;
                case "Protein": {
                    goal = ((UHealthApplication)getApplication()).getGoals().getFoodProtein();
                }
                break;
                case "Fat": {
                    goal = ((UHealthApplication)getApplication()).getGoals().getFoodFat();
                }
                break;
                default: {
                    goal = 0;
                }
                break;

            }
            final Calendar actDayStart = (Calendar) mPeriodStart.clone();
            actDayStart.set(Calendar.HOUR_OF_DAY, 0);
            actDayStart.set(Calendar.MINUTE, 0);
            actDayStart.set(Calendar.SECOND, 0);
            actDayStart.set(Calendar.MILLISECOND, 0);

            final Calendar actDayEnd = (Calendar) mPeriodEnd.clone();
            actDayEnd.set(Calendar.HOUR_OF_DAY, 0);
            actDayEnd.set(Calendar.MINUTE, 0);
            actDayEnd.set(Calendar.SECOND, 0);
            actDayEnd.set(Calendar.MILLISECOND, 0);

            setGraph(category, mBinding.graphFood, points, goal, actDayStart, actDayEnd, i);
        }
    }

    private void updateWaterGraph() {
        String waterSpinnerText = mBinding.spinnerWater.getSelectedItem().toString();
        updateWaterGraph(waterSpinnerText, (Calendar) mPeriodStart.clone(), new ArrayList<Entry>(), 0, 0);
    }

    private void updateWaterGraph(final String category, final Calendar actDay, final ArrayList<Entry> points, final int i, final double maxY) {
        if (actDay.before(mPeriodEnd)) {
            final Calendar actDayStart = (Calendar) actDay.clone();
            actDayStart.set(Calendar.HOUR_OF_DAY, 0);
            actDayStart.set(Calendar.MINUTE, 0);
            actDayStart.set(Calendar.SECOND, 0);
            actDayStart.set(Calendar.MILLISECOND, 0);

            final Calendar actDayEnd = (Calendar) actDayStart.clone();
            actDayEnd.set(Calendar.HOUR_OF_DAY, 23);
            actDayEnd.set(Calendar.MINUTE, 59);
            actDayEnd.set(Calendar.SECOND, 59);
            actDayEnd.set(Calendar.MILLISECOND, 999);

            final WaterModule.OnWaterDrunkListResultListener listener = new WaterModule.OnWaterDrunkListResultListener() {
                @Override
                public void onResult(ArrayList<WaterDrunk> result) {
                    long quantity = 0;
                    for (WaterDrunk wd : result)
                        quantity += wd.getQuantity();


                    points.add(new Entry(actDayStart.getTimeInMillis(), quantity / 1000f));

                    actDay.add(Calendar.DAY_OF_YEAR, 1);
                    updateWaterGraph(category, actDay, points, i + 1, maxY > quantity ? maxY : quantity);
                }
            };

            switch (category) {
                case "Quantity": {
                    mWaterModule.getWaterByPeriod(actDay.getTimeInMillis(), actDayEnd.getTimeInMillis(), listener);
                }
                break;
                default: {
                    updateWaterGraph(category, actDay, points, i + 1, maxY);
                }
                break;
            }

        } else {
            final float goal;

            switch (category) {
                case "Quantity": {
                    goal = ((UHealthApplication)getApplication()).getGoals().getWaterQuantity();
                }
                break;
                default: {
                    goal = 0;
                }
                break;

            }
            final Calendar actDayStart = (Calendar) mPeriodStart.clone();
            actDayStart.set(Calendar.HOUR_OF_DAY, 0);
            actDayStart.set(Calendar.MINUTE, 0);
            actDayStart.set(Calendar.SECOND, 0);
            actDayStart.set(Calendar.MILLISECOND, 0);

            final Calendar actDayEnd = (Calendar) mPeriodEnd.clone();
            actDayEnd.set(Calendar.HOUR_OF_DAY, 0);
            actDayEnd.set(Calendar.MINUTE, 0);
            actDayEnd.set(Calendar.SECOND, 0);
            actDayEnd.set(Calendar.MILLISECOND, 0);

            setGraph(category, mBinding.graphWater, points, goal, actDayStart, actDayEnd, i);
        }
    }

    private void updateSportGraph() {
        String sportSpinnerText = mBinding.spinnerSport.getSelectedItem().toString();
        updateSportGraph(sportSpinnerText, (Calendar) mPeriodStart.clone(), new ArrayList<Entry>(), 0, 0);
    }

    private void updateSportGraph(final String category, final Calendar actDay, final ArrayList<Entry> points, final int i, final double maxY) {
        if (actDay.before(mPeriodEnd)) {
            final Calendar actDayStart = (Calendar) actDay.clone();
            actDayStart.set(Calendar.HOUR_OF_DAY, 0);
            actDayStart.set(Calendar.MINUTE, 0);
            actDayStart.set(Calendar.SECOND, 0);
            actDayStart.set(Calendar.MILLISECOND, 0);

            final Calendar actDayEnd = (Calendar) actDayStart.clone();
            actDayEnd.set(Calendar.HOUR_OF_DAY, 23);
            actDayEnd.set(Calendar.MINUTE, 59);
            actDayEnd.set(Calendar.SECOND, 59);
            actDayEnd.set(Calendar.MILLISECOND, 999);


            final GoogleFitModule.OnFloatResultListener listenerFloat = new GoogleFitModule.OnFloatResultListener() {
                @Override
                public void onResult(float result) {
                    points.add(new Entry(actDayStart.getTimeInMillis(), result));
                    actDay.add(Calendar.DAY_OF_YEAR, 1);
                    updateSportGraph(category, actDay, points, i + 1, maxY > result ? maxY : result);
                }
            };
            final GoogleFitModule.OnLongResultListener listenerLong = new GoogleFitModule.OnLongResultListener() {
                @Override
                public void onResult(long result) {
                    points.add(new Entry(actDayStart.getTimeInMillis(), (float) result));
                    actDay.add(Calendar.DAY_OF_YEAR, 1);
                    updateSportGraph(category, actDay, points, i + 1, maxY > result ? maxY : result);
                }
            };

            switch (category) {
                case "Distance": {
                    mGoogleFitModule.getDistance(actDay.getTimeInMillis(), actDayEnd.getTimeInMillis(), listenerFloat);
                }
                break;
                case "Duration": {
                    mGoogleFitModule.getDuration(actDay.getTimeInMillis(), actDayEnd.getTimeInMillis(), listenerLong);
                }
                break;
                case "Calories": {
                    mGoogleFitModule.getCalories(actDay.getTimeInMillis(), actDayEnd.getTimeInMillis(), listenerFloat);
                }
                break;
                case "Steps": {
                    mGoogleFitModule.getSteps(actDay.getTimeInMillis(), actDayEnd.getTimeInMillis(), listenerLong);
                }
                break;
                default: {
                    updateSportGraph(category, actDay, points, i + 1, maxY);
                }
                break;
            }
        } else {
            final float goal;

            switch (category) {
                case "Distance": {
                    goal = ((UHealthApplication)getApplication()).getGoals().getSportDistance() * 1000;
                }
                break;
                case "Duration": {
                    goal = ((UHealthApplication)getApplication()).getGoals().getSportDuration();
                }
                break;
                case "Calories": {
                    goal = ((UHealthApplication)getApplication()).getGoals().getSportCalories();
                }
                break;
                case "Steps": {
                    goal = ((UHealthApplication)getApplication()).getGoals().getSportSteps();
                }
                break;
                default: {
                    goal = 0;
                }
                break;

            }
            final Calendar actDayStart = (Calendar) mPeriodStart.clone();
            actDayStart.set(Calendar.HOUR_OF_DAY, 0);
            actDayStart.set(Calendar.MINUTE, 0);
            actDayStart.set(Calendar.SECOND, 0);
            actDayStart.set(Calendar.MILLISECOND, 0);

            final Calendar actDayEnd = (Calendar) mPeriodEnd.clone();
            actDayEnd.set(Calendar.HOUR_OF_DAY, 0);
            actDayEnd.set(Calendar.MINUTE, 0);
            actDayEnd.set(Calendar.SECOND, 0);
            actDayEnd.set(Calendar.MILLISECOND, 0);

            setGraph(category, mBinding.graphSport, points, goal, actDayStart, actDayEnd, i);
        }
    }

    private void setGraph(final String name, final LineChart graph, final ArrayList<Entry> points, final float goal, final Calendar start, final Calendar end, final int days) {
        LineDataSet dataSet = new LineDataSet(points, name);
        dataSet.setColor(ResourcesCompat.getColor(getResources(), R.color.darkAccent, null));
        dataSet.setCircleColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
        dataSet.setDrawCircles(true);
        dataSet.setDrawCircleHole(false);
        dataSet.setCircleRadius(5);
        dataSet.setLineWidth(2);

        ArrayList<Entry> goalEntry = new ArrayList<>();
        goalEntry.add(new Entry(start.getTimeInMillis(), goal));
        goalEntry.add(new Entry(end.getTimeInMillis(), goal));

        LineDataSet goalDataSet = new LineDataSet(goalEntry, "Goal");
        goalDataSet.setColor(Color.RED);
        goalDataSet.setDrawCircles(false);
        goalDataSet.setDrawCircleHole(false);
        goalDataSet.setLineWidth(1);

        final LineData lineData = new LineData(dataSet);
        if (goal > 0)
            lineData.addDataSet(goalDataSet);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                graph.getDescription().setEnabled(false);

                graph.setTouchEnabled(true);
                graph.setDragEnabled(true);
                graph.setScaleEnabled(false);
                graph.setScaleXEnabled(true);
                graph.setScaleYEnabled(false);
                graph.setPinchZoom(true);
                graph.setDoubleTapToZoomEnabled(false);

                graph.getXAxis().setAxisMinimum(lineData.getXMin());
                graph.getXAxis().setAxisMaximum(lineData.getXMax());
                graph.setXAxisRenderer(new MyXAxisRenderer(graph.getViewPortHandler(), graph.getXAxis(), graph.getTransformer(YAxis.AxisDependency.LEFT), days));

                graph.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
                        return sdf.format(new Date((long) value + 1000 * 60 * 60 * 12));
                    }
                });

                graph.getAxisLeft().setEnabled(true);
                float minY = lineData.getYMin();
                graph.getAxisLeft().setAxisMinimum(minY);
                float maxY = lineData.getYMax() + (lineData.getYMax() - lineData.getYMin()) / 6f;
                graph.getAxisLeft().setAxisMaximum(maxY > minY ? maxY : 1);

                graph.getAxisRight().setEnabled(false);

                graph.setData(lineData);
                graph.invalidate();
            }
        });
    }

    public boolean onSupportNavigateUp() {
        closeActivity();
        return true;
    }

    private class MyXAxisRenderer extends XAxisRenderer {
        private int mDays;
        private ViewPortHandler mViewPortHandler;

        MyXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans, int days) {
            super(viewPortHandler, xAxis, trans);
            this.mViewPortHandler = viewPortHandler;
            this.mDays = days;
        }

        public int getEntryCount() {
            return mAxis.mEntryCount;
        }

        @Override
        protected void computeAxisValues(float min, float max) {
            double range = Math.abs(mXAxis.getAxisMaximum() - mXAxis.getAxisMinimum());

            if (mDays == 0 || range <= 0 || Double.isInfinite(range)) {
                mAxis.mEntries = new float[]{};
                mAxis.mCenteredEntries = new float[]{};
                mAxis.mEntryCount = 0;
                return;
            }

            int divider = 1;
            while (mDays / divider > 12)
                divider++;

            float zoom = mViewPortHandler.getScaleX() > 1f ? mViewPortHandler.getScaleX() * 0.5f : 1f;
            divider = (int) ((float) divider / zoom);
            if (divider < 1)
                divider = 1;

            int labelCount = mDays / divider;

            mAxis.mEntryCount = labelCount;
            if (mAxis.mEntries.length < labelCount)
                mAxis.mEntries = new float[labelCount];

            double interval = (float) range / (float) (labelCount - 1);

            float v = mXAxis.getAxisMinimum();
            for (int i = 0; i < labelCount; i++) {
                mAxis.mEntries[i] = v;
                if (divider > 1)
                    v += interval;
                else
                    v += interval;
            }

            computeSize();
        }
    }
}

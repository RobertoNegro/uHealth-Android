package com.negroroberto.uhealth.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.abstracts.UHealthActivity;
import com.negroroberto.uhealth.activities.abstracts.UnityActivity;
import com.negroroberto.uhealth.databinding.ActivityWaterCreationBinding;
import com.negroroberto.uhealth.modules.water.models.WaterDrunk;
import com.negroroberto.uhealth.unity.UnityController;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.Extras;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WaterCreationActivity extends UnityActivity {
    private ActivityWaterCreationBinding mBinding;
    private WaterDrunk mEditingWater;
    private Calendar mCalendar;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_water_creation);
        setResult(RESULT_CANCELED);

        mBinding.waterBottle.setTotalValue(1500);
        mBinding.waterBottle.setActualValue(1500);
        mBinding.waterGlass.setTotalValue(220);
        mBinding.waterGlass.setActualValue(220);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mCalendar = (Calendar)getIntent().getSerializableExtra(Extras.EXTRA_CALENDAR);
        mEditingWater = getIntent().getParcelableExtra(Extras.EXTRA_WATER);

        if (mEditingWater != null) {
            mBinding.waterGlass.setActualValue(mEditingWater.getQuantity());
            mBinding.waterBottle.setActualValue(mEditingWater.getQuantity());
            mBinding.txtWater.setText(Common.DoubleStringify(mEditingWater.getQuantity()));

            mBinding.txtActualWaterGlass.setText(Common.DoubleStringify(mBinding.waterGlass.getActualValue()));
            mBinding.txtActualWaterBottle.setText(Common.DoubleStringify(mBinding.waterBottle.getActualValue()));

            if(mEditingWater.getQuantity() <= 220) {
                switchGlassCard();
                mBinding.radioGlass.setChecked(true);
            } else if(mEditingWater.getQuantity() <= 1500) {
                switchBottleCard();
                mBinding.radioBottle.setChecked(true);
            } else {
                switchManualCard();
                mBinding.radioManual.setChecked(true);
            }

            mCalendar = mEditingWater.getTime();

            mBinding.btnCreate.setText("Update");
        } else {
            if(mCalendar == null) {
                mCalendar = Calendar.getInstance();
                mCalendar.setTime(new Date());
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);
            }
        }

        updateDateTime();

        mBinding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClicked();
            }
        });

        final Calendar actualCal = Calendar.getInstance();
        actualCal.setTime(new Date());
        mBinding.btnChangeDatetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(WaterCreationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        if(mCalendar.compareTo(actualCal) > 0) {
                            mCalendar.setTime(actualCal.getTime());
                            mCalendar.set(Calendar.SECOND, 0);
                            mCalendar.set(Calendar.MILLISECOND, 0);
                        }

                        updateDateTime();

                        TimePickerDialog timePickerDialog = new TimePickerDialog(WaterCreationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                mCalendar.set(Calendar.MINUTE, minute);
                                mCalendar.set(Calendar.SECOND, 0);
                                mCalendar.set(Calendar.MILLISECOND, 0);

                                if(mCalendar.compareTo(actualCal) > 0) {
                                    mCalendar.setTime(actualCal.getTime());
                                    mCalendar.set(Calendar.SECOND, 0);
                                    mCalendar.set(Calendar.MILLISECOND, 0);
                                }

                                updateDateTime();
                            }
                        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);

                        timePickerDialog.show();
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        mBinding.waterGlass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mBinding.scrollView.requestDisallowInterceptTouchEvent(false);
                        v.performClick();
                        break;

                    case MotionEvent.ACTION_DOWN:
                        mBinding.scrollView.requestDisallowInterceptTouchEvent(true);
                    case MotionEvent.ACTION_MOVE:
                        mBinding.scrollView.requestDisallowInterceptTouchEvent(true);
                        double perc = 1f - event.getY() / mBinding.waterGlass.getHeight();
                        double value = 220d * perc;
                        mBinding.waterGlass.setActualValue(value);
                        mBinding.txtActualWaterGlass.setText(Common.DoubleStringify(mBinding.waterGlass.getActualValue()));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        mBinding.waterBottle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        mBinding.scrollView.requestDisallowInterceptTouchEvent(false);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        mBinding.scrollView.requestDisallowInterceptTouchEvent(true);
                    case MotionEvent.ACTION_MOVE:
                        mBinding.scrollView.requestDisallowInterceptTouchEvent(true);
                        double perc = 1f - event.getY() / mBinding.waterBottle.getHeight();
                        double value = 1500d * perc;
                        mBinding.waterBottle.setActualValue(value);
                        mBinding.txtActualWaterBottle.setText(Common.DoubleStringify(mBinding.waterBottle.getActualValue()));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        mBinding.radioBottle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    switchBottleCard();
            }
        });
        mBinding.radioGlass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    switchGlassCard();
            }
        });
        mBinding.radioManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    switchManualCard();
            }
        });

        mBinding.idUnity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WaterCreationActivity.this, SceneActivity.class);
                startActivity(intent);
            }
        });

        if(getIntent().getBooleanExtra(Extras.EXTRA_FIRST_RUN, false))
            sendMessageToUser("From here you can set how much water you've drunk and when you've drunk it. Select from a glass, a bottle or type in manually the quantity. If you choose one of the first two, tap and drag the water to manually set how much you've actually drunk.", null);
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


    @Override
    protected void onPause() {
        super.onPause();
        mBinding.waterBottle.pauseAnimation();
        mBinding.waterGlass.pauseAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.waterBottle.resumeAnimation();
        mBinding.waterGlass.resumeAnimation();

    }

    private void createClicked() {
        final WaterDrunk water = new WaterDrunk();

        if (mEditingWater != null)
            water.setId(mEditingWater.getId());


        boolean valid = true;
        try {
            double quantity = 0;
            if(mBinding.radioManual.isChecked())
                quantity = Common.ParseDouble(mBinding.txtWater.getText().toString());
            else if(mBinding.radioGlass.isChecked())
                quantity = mBinding.waterGlass.getActualValue();
            else if(mBinding.radioBottle.isChecked())
                quantity = mBinding.waterBottle.getActualValue();
            else
                valid = false;

            water.setQuantity((long)(quantity));
            water.setTime(mCalendar);
        } catch (NumberFormatException | NullPointerException ignore) {
            valid = false;
        }

        if (!valid)
            Toast.makeText(WaterCreationActivity.this, "Something went wrong. Please, check the fields.", Toast.LENGTH_SHORT).show();
        else {
            Intent result = new Intent();
            result.putExtra(Extras.EXTRA_WATER, (Parcelable) water);
            setResult(RESULT_OK, result);
            closeActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_water_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_create: {
                createClicked();
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        mBinding.txtDatetime.setText(sdf.format(mCalendar.getTime()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        closeActivity();
        return true;
    }

    private void switchGlassCard() {
        mBinding.layoutGlass.setVisibility(View.VISIBLE);
        mBinding.layoutBottle.setVisibility(View.GONE);
        mBinding.layoutManual.setVisibility(View.GONE);
    }

    private void switchBottleCard() {
        mBinding.layoutGlass.setVisibility(View.GONE);
        mBinding.layoutBottle.setVisibility(View.VISIBLE);
        mBinding.layoutManual.setVisibility(View.GONE);
    }

    private void switchManualCard() {
        mBinding.layoutGlass.setVisibility(View.GONE);
        mBinding.layoutBottle.setVisibility(View.GONE);
        mBinding.layoutManual.setVisibility(View.VISIBLE);
    }
}

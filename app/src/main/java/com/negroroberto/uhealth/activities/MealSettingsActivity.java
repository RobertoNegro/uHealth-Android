package com.negroroberto.uhealth.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.abstracts.UHealthActivity;
import com.negroroberto.uhealth.activities.abstracts.UnityActivity;
import com.negroroberto.uhealth.databinding.ActivityMealSettingsBinding;
import com.negroroberto.uhealth.modules.food.models.Meal;
import com.negroroberto.uhealth.unity.UnityController;
import com.negroroberto.uhealth.utils.Debug;
import com.negroroberto.uhealth.utils.Extras;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MealSettingsActivity extends UnityActivity {
    private ActivityMealSettingsBinding mBinding;
    private Meal mMeal;
    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_meal_settings);

        setResult(RESULT_CANCELED);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mMeal = getIntent().getParcelableExtra(Extras.EXTRA_MEAL);
        if (mMeal == null) {
            Toast.makeText(this, "Something went wrong..", Toast.LENGTH_SHORT).show();
            Debug.Warn(this, "Null meal on meal settings activity");
            finish();
        }

        mCalendar = Calendar.getInstance();
        mCalendar.setTime(mMeal.getTime().getTime());

        mBinding.txtName.setText(mMeal.getName());
        updateDateTime();


        final Calendar actualCal = Calendar.getInstance();
        actualCal.setTime(new Date());

        mBinding.btnChangeDatetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MealSettingsActivity.this, new DatePickerDialog.OnDateSetListener() {
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

                        TimePickerDialog timePickerDialog = new TimePickerDialog(MealSettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

        mBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });

        mBinding.idUnity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MealSettingsActivity.this, SceneActivity.class);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meal_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_apply: {
                applyChanges();
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void applyChanges() {
        mMeal.setName(mBinding.txtName.getText().toString());
        mMeal.setTime(mCalendar);

        Intent result = new Intent();
        result.putExtra(Extras.EXTRA_MEAL, (Parcelable)mMeal);
        setResult(RESULT_OK, result);

        onClosing();
        finish();
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

    @Override
    protected void closeActivity() {
        if(!mMeal.getName().equals(mBinding.txtName.getText().toString()) || mCalendar.compareTo(mMeal.getTime()) != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle("Are you sure?")
                    .setMessage("If you go back, all your unsaved changes will be lost.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            onClosing();
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            onClosing();
            finish();
        }
    }

}
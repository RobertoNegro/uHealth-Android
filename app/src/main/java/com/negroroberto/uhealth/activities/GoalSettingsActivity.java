package com.negroroberto.uhealth.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.abstracts.UHealthActivity;
import com.negroroberto.uhealth.activities.abstracts.UnityActivity;
import com.negroroberto.uhealth.databinding.ActivityGoalSettingsBinding;
import com.negroroberto.uhealth.unity.UnityController;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.Extras;

public class GoalSettingsActivity extends UnityActivity {
    private ActivityGoalSettingsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_goal_settings);

        setResult(RESULT_CANCELED);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });

        mBinding.chkFoodCalories.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.containerFoodCalories.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        mBinding.chkFoodCalories.setChecked(((UHealthApplication) getApplication()).getGoals().getFoodCalories() > 0);
        mBinding.txtFoodCalories.setText(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getFoodCalories()));

        mBinding.chkFoodCarbs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.containerFoodCarbs.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        mBinding.chkFoodCarbs.setChecked(((UHealthApplication)getApplication()).getGoals().getFoodCarbs() > 0);
        mBinding.txtFoodCarbs.setText(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getFoodCarbs()));

        mBinding.chkFoodFat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.containerFoodFat.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        mBinding.chkFoodFat.setChecked(((UHealthApplication)getApplication()).getGoals().getFoodFat() > 0);
        mBinding.txtFoodFat.setText(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getFoodFat()));

        mBinding.chkFoodProtein.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.containerFoodProtein.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        mBinding.chkFoodProtein.setChecked(((UHealthApplication)getApplication()).getGoals().getFoodProtein() > 0);
        mBinding.txtFoodProtein.setText(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getFoodProtein()));

        mBinding.chkWaterQuantity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.containerWaterQuantity.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        mBinding.chkWaterQuantity.setChecked(((UHealthApplication)getApplication()).getGoals().getWaterQuantity() > 0);
        mBinding.txtWaterQuantity.setText(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getWaterQuantity()));

        mBinding.chkSportDuration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.containerSportDuration.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        mBinding.chkSportDuration.setChecked(((UHealthApplication)getApplication()).getGoals().getSportDuration() > 0);
        mBinding.txtSportDuration.setText(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getSportDuration()));

        mBinding.chkSportDistance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.containerSportDistance.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        mBinding.chkSportDistance.setChecked(((UHealthApplication)getApplication()).getGoals().getSportDistance() > 0);
        mBinding.txtSportDistance.setText(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getSportDistance()));

        mBinding.chkSportCalories.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.containerSportCalories.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        mBinding.chkSportCalories.setChecked(((UHealthApplication)getApplication()).getGoals().getSportCalories() > 0);
        mBinding.txtSportCalories.setText(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getSportCalories()));

        mBinding.chkSportSteps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.containerSportSteps.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        mBinding.chkSportSteps.setChecked(((UHealthApplication)getApplication()).getGoals().getSportSteps() > 0);
        mBinding.txtSportSteps.setText(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getSportSteps()));

        mBinding.idUnity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoalSettingsActivity.this, SceneActivity.class);
                startActivity(intent);
            }
        });

        if(getIntent().getBooleanExtra(Extras.EXTRA_FIRST_RUN, false)) {
            sendMessageToUser("Choose from what you need, and I'll help you to reach those values.", null);
        }
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
        getMenuInflater().inflate(R.menu.menu_goal_settings, menu);
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
        boolean valid = true;
        try {
            if (mBinding.chkFoodCalories.isChecked())
                ((UHealthApplication)getApplication()).getGoals().setFoodCalories((float) Common.ParseDouble(mBinding.txtFoodCalories.getText().toString()));
            else
                ((UHealthApplication)getApplication()).getGoals().setFoodCalories(0);

            if (mBinding.chkFoodCarbs.isChecked())
                ((UHealthApplication)getApplication()).getGoals().setFoodCarbs((float) Common.ParseDouble(mBinding.txtFoodCarbs.getText().toString()));
            else
                ((UHealthApplication)getApplication()).getGoals().setFoodCarbs(0);

            if (mBinding.chkFoodFat.isChecked())
                ((UHealthApplication)getApplication()).getGoals().setFoodFat((float) Common.ParseDouble(mBinding.txtFoodFat.getText().toString()));
            else
                ((UHealthApplication)getApplication()).getGoals().setFoodFat(0);

            if (mBinding.chkFoodProtein.isChecked())
                ((UHealthApplication)getApplication()).getGoals().setFoodProtein((float) Common.ParseDouble(mBinding.txtFoodProtein.getText().toString()));
            else
                ((UHealthApplication)getApplication()).getGoals().setFoodProtein(0);

            if (mBinding.chkWaterQuantity.isChecked())
                ((UHealthApplication)getApplication()).getGoals().setWaterQuantity((float) Common.ParseDouble(mBinding.txtWaterQuantity.getText().toString()));
            else
                ((UHealthApplication)getApplication()).getGoals().setWaterQuantity(0);

            if (mBinding.chkSportDuration.isChecked())
                ((UHealthApplication)getApplication()).getGoals().setSportDuration((float) Common.ParseDouble(mBinding.txtSportDuration.getText().toString()));
            else
                ((UHealthApplication)getApplication()).getGoals().setSportDuration(0);

            if (mBinding.chkSportDistance.isChecked())
                ((UHealthApplication)getApplication()).getGoals().setSportDistance((float) Common.ParseDouble(mBinding.txtSportDistance.getText().toString()));
            else
                ((UHealthApplication)getApplication()).getGoals().setSportDistance(0);

            if (mBinding.chkSportCalories.isChecked())
                ((UHealthApplication)getApplication()).getGoals().setSportCalories((float) Common.ParseDouble(mBinding.txtSportCalories.getText().toString()));
            else
                ((UHealthApplication)getApplication()).getGoals().setSportCalories(0);

            if (mBinding.chkSportSteps.isChecked())
                ((UHealthApplication)getApplication()).getGoals().setSportSteps((float) Common.ParseDouble(mBinding.txtSportSteps.getText().toString()));
            else
                ((UHealthApplication)getApplication()).getGoals().setSportSteps(0);
        } catch (NumberFormatException | NullPointerException e) {
            valid = false;
        }

        if (valid) {
            setResult(RESULT_OK);
            onClosing();
            finish();
        } else
            Toast.makeText(GoalSettingsActivity.this, "Something went wrong. Please, check the fields.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        closeActivity();
        return true;
    }


    private boolean checkChanges() {
        boolean invariated = true;

        invariated = invariated && ((!mBinding.chkFoodCalories.isChecked() && ((UHealthApplication)getApplication()).getGoals().getFoodCalories() == 0) || (mBinding.chkFoodCalories.isChecked() && mBinding.txtFoodCalories.getText().toString().equals(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getFoodCalories()))));
        invariated = invariated && ((!mBinding.chkFoodCarbs.isChecked() && ((UHealthApplication)getApplication()).getGoals().getFoodCarbs() == 0) || (mBinding.chkFoodCarbs.isChecked() && mBinding.txtFoodCarbs.getText().toString().equals(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getFoodCarbs()))));
        invariated = invariated && ((!mBinding.chkFoodFat.isChecked() && ((UHealthApplication)getApplication()).getGoals().getFoodFat() == 0) || (mBinding.chkFoodFat.isChecked() && mBinding.txtFoodFat.getText().toString().equals(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getFoodFat()))));
        invariated = invariated && ((!mBinding.chkFoodProtein.isChecked() && ((UHealthApplication)getApplication()).getGoals().getFoodProtein() == 0) || (mBinding.chkFoodProtein.isChecked() && mBinding.txtFoodProtein.getText().toString().equals(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getFoodProtein()))));

        invariated = invariated && ((!mBinding.chkWaterQuantity.isChecked() && ((UHealthApplication)getApplication()).getGoals().getWaterQuantity() == 0) || (mBinding.chkWaterQuantity.isChecked() && mBinding.txtWaterQuantity.getText().toString().equals(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getWaterQuantity()))));

        invariated = invariated && ((!mBinding.chkSportDuration.isChecked() && ((UHealthApplication)getApplication()).getGoals().getSportDuration() == 0) || (mBinding.chkSportDuration.isChecked() && mBinding.txtSportDuration.getText().toString().equals(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getSportDuration()))));
        invariated = invariated && ((!mBinding.chkSportDistance.isChecked() && ((UHealthApplication)getApplication()).getGoals().getSportDistance() == 0) || (mBinding.chkSportDistance.isChecked() && mBinding.txtSportDistance.getText().toString().equals(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getSportDistance()))));
        invariated = invariated && ((!mBinding.chkSportCalories.isChecked() && ((UHealthApplication)getApplication()).getGoals().getSportCalories() == 0) || (mBinding.chkSportCalories.isChecked() && mBinding.txtSportCalories.getText().toString().equals(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getSportCalories()))));
        invariated = invariated && ((!mBinding.chkSportSteps.isChecked() && ((UHealthApplication)getApplication()).getGoals().getSportSteps() == 0) || (mBinding.chkSportSteps.isChecked() && mBinding.txtSportSteps.getText().toString().equals(Common.DoubleStringify(((UHealthApplication)getApplication()).getGoals().getSportSteps()))));

        return !invariated;
    }

    @Override
    protected void closeActivity() {
        if (checkChanges()) {
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
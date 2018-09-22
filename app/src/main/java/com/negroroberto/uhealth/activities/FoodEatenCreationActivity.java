package com.negroroberto.uhealth.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.negroroberto.uhealth.databinding.ActivityFoodEatenCreationBinding;
import com.negroroberto.uhealth.modules.food.FoodModule;
import com.negroroberto.uhealth.modules.food.models.Food;
import com.negroroberto.uhealth.modules.food.models.FoodEaten;
import com.negroroberto.uhealth.unity.UnityController;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.Debug;
import com.negroroberto.uhealth.utils.Extras;

import java.util.ArrayList;

public class FoodEatenCreationActivity extends UnityActivity {
    private ActivityFoodEatenCreationBinding mBinding;
    private FoodModule mFoodModule;

    private long mFoodId;
    private double mPortion;
    private FoodEaten mIsEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_food_eaten_creation);
        setResult(RESULT_CANCELED);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mIsEditing = getIntent().getParcelableExtra(Extras.EXTRA_FOOD_EATEN);
        mFoodId = getIntent().getLongExtra(Extras.EXTRA_FOOD_ID, Long.MIN_VALUE);

        mFoodModule = new FoodModule((UHealthApplication)getApplication());

        long id = Long.MIN_VALUE;
        if(mFoodId != Long.MIN_VALUE)
            id = mFoodId;
        else if(mIsEditing != null)
            id = mIsEditing.getFoodId();

        if (id == Long.MIN_VALUE) {
            Debug.Err(this, "No food id to add and no foodeaten to edit on intent!");
            finish();
        }

        mPortion = 0;
        mFoodModule.getFoodsById(new FoodModule.OnFoodsListResultListener() {
            @Override
            public void onResult(ArrayList<Food> result) {
                if (result.size() > 0) {
                    Food f = result.get(0);
                    mPortion = f.getServingQuantity();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsEditing == null) {
                                mBinding.txtGrams.setText(Common.DoubleStringify(mPortion));
                                mBinding.txtPortions.setText("1");
                            } else {
                                mBinding.txtGrams.setText(Common.DoubleStringify(mIsEditing.getQuantity()));
                                mBinding.txtPortions.setText(Common.DoubleStringify(mIsEditing.getQuantity() / mPortion));
                            }
                            updateTexts();
                        }
                    });
                }
            }
        }, id);

        mBinding.radioPortions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBinding.layoutPortions.setVisibility(View.VISIBLE);
                } else {
                    mBinding.layoutPortions.setVisibility(View.GONE);
                }
            }
        });

        mBinding.radioGrams.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBinding.layoutGrams.setVisibility(View.VISIBLE);
                } else {
                    mBinding.layoutGrams.setVisibility(View.GONE);
                }
            }
        });

        mBinding.txtGrams.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTexts();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBinding.txtPortions.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTexts();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.btnPortionQuarter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.txtPortions.setText(Common.DoubleStringify(0.25d));
            }
        });

        mBinding.btnPortionHalf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.txtPortions.setText(Common.DoubleStringify(0.5d));
            }
        });

        mBinding.btnPortionOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.txtPortions.setText(Common.DoubleStringify(1d));
            }
        });

        mBinding.btnPortionTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.txtPortions.setText(Common.DoubleStringify(2d));
            }
        });

        mBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClicked();
            }
        });

        mBinding.idUnity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodEatenCreationActivity.this, SceneActivity.class);
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


    private void saveClicked() {
        if(mIsEditing == null) {
            if (!createFoodEaten())
                Toast.makeText(FoodEatenCreationActivity.this, "Sorry, something went wrong. Please, check your inputs and try again.", Toast.LENGTH_SHORT).show();
        } else {
            if (!updateFoodEaten())
                Toast.makeText(FoodEatenCreationActivity.this, "Sorry, something went wrong. Please, check your inputs and try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_food_eaten_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_save: {
                saveClicked();
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        closeActivity();
        return true;
    }

    private double getQuantity() {
        double quantity = -Double.MAX_VALUE;

        if (mBinding.radioGrams.isChecked()) {
            try {
                quantity = Common.ParseDouble(mBinding.txtGrams.getText().toString());
            } catch (NumberFormatException | NullPointerException ignore) {
            }
        } else if (mBinding.radioPortions.isChecked()) {
            try {
                quantity = Common.ParseDouble(mBinding.txtPortions.getText().toString()) * mPortion;
            } catch (NumberFormatException | NullPointerException ignore) {
            }
        }

        return quantity;
    }

    private boolean updateFoodEaten() {
        double quantity = getQuantity();
        if (quantity <= 0)
            return false;

        if(mIsEditing == null)
            return false;

        mIsEditing.setQuantity(quantity);
        mFoodModule.updateFoodEaten(new FoodModule.OnVoidResultListener() {
            @Override
            public void onResult() {
                Intent intent = new Intent();
                intent.putExtra(Extras.EXTRA_FOOD_EATEN_UPDATED, true);
                setResult(RESULT_OK, intent);
                closeActivity();
            }
        }, mIsEditing);

        return true;
    }

    private boolean createFoodEaten() {
        double quantity = getQuantity();
        if (quantity <= 0)
            return false;

        FoodEaten fe = new FoodEaten();
        fe.setFoodId(mFoodId);
        fe.setQuantity(quantity);

        mFoodModule.createFoodEaten(new FoodModule.OnLongResultListener() {
            @Override
            public void onResult(long result) {
                Intent intent = new Intent();
                intent.putExtra(Extras.EXTRA_FOOD_EATEN_ID, result);
                setResult(RESULT_OK, intent);
                closeActivity();
            }
        }, fe);
        return true;
    }

    private void updateTexts() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                double actValue = -Double.MAX_VALUE;
                try {
                    actValue = Common.ParseDouble(mBinding.txtPortions.getText().toString());
                } catch (NumberFormatException | NullPointerException ignore) {
                }

                if (actValue == -Double.MAX_VALUE)
                    mBinding.txtPortionDesc.setText("Invalid value");
                else
                    mBinding.txtPortionDesc.setText("Each portion is made of " + Common.DoubleStringify(mPortion) + " grams. In total, you ate " + Common.DoubleStringify(actValue * mPortion) + " grams of the selected product.");

                actValue = -Double.MAX_VALUE;
                try {
                    actValue = Common.ParseDouble(mBinding.txtGrams.getText().toString());
                } catch (NumberFormatException | NullPointerException ignore) {
                }

                if (actValue == -Double.MAX_VALUE)
                    mBinding.txtGramsDesc.setText("Invalid value");
                else
                    mBinding.txtGramsDesc.setText("Each portion is made of " + Common.DoubleStringify(mPortion) + " grams. In total, you ate " + Common.DoubleStringify(actValue / mPortion) + " portions of the selected product.");
            }
        });
    }
}

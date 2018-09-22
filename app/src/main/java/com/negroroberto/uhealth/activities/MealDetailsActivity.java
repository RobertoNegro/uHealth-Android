package com.negroroberto.uhealth.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.abstracts.UHealthActivity;
import com.negroroberto.uhealth.activities.abstracts.UnityActivity;
import com.negroroberto.uhealth.activities.adapters.FoodsOnMealAdapter;
import com.negroroberto.uhealth.databinding.ActivityMealDetailsBinding;
import com.negroroberto.uhealth.modules.food.FoodModule;
import com.negroroberto.uhealth.modules.food.models.Food;
import com.negroroberto.uhealth.modules.food.models.FoodEaten;
import com.negroroberto.uhealth.modules.food.models.Meal;
import com.negroroberto.uhealth.unity.UnityController;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.Debug;
import com.negroroberto.uhealth.utils.Extras;
import com.negroroberto.uhealth.utils.RequestCodes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MealDetailsActivity extends UnityActivity implements FoodsOnMealAdapter.OnFoodSettingsClick, FoodsOnMealAdapter.OnFoodTrashClick {
    private ActivityMealDetailsBinding mBinding;
    private Meal mMeal;
    private FoodModule mFoodModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_meal_details);
        setResult(RESULT_CANCELED);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFoodModule = new FoodModule((UHealthApplication)getApplication());

        mMeal = getIntent().getParcelableExtra(Extras.EXTRA_MEAL);
        if (mMeal == null) {
            Toast.makeText(this, "Something went wrong..", Toast.LENGTH_SHORT).show();
            Debug.Warn(this, "Null meal on meal details activity");
            finish();
        }

        ViewGroup footer = (ViewGroup) getLayoutInflater().inflate(R.layout.listitem_plus_footer, mBinding.listFood, false);
        mBinding.listFood.addFooterView(footer, null, true);
        footer.findViewById(R.id.btnFooterPlus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood();
            }
        });

        mBinding.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        mBinding.btnTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMeal();
            }
        });

        updateInfos();

        mBinding.idUnity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MealDetailsActivity.this, SceneActivity.class);
                startActivity(intent);
            }
        });

        if(getIntent().getBooleanExtra(Extras.EXTRA_FIRST_RUN, false))
            sendMessageToUser("Here you can set the list of the foods eaten in this meal, and change its settings. For example, you can add a new food tapping on the gray plus symbol in the empty list, or clicking on Add food inside the top right menu. You can either change the name of the meal or the date and the time of the meal itself. Come back tapping on me when you've done.", null);
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


    private void openSettings() {
        Intent intent = new Intent(MealDetailsActivity.this, MealSettingsActivity.class);
        intent.putExtra(Extras.EXTRA_MEAL, (Parcelable) mMeal);

        startActivityForResult(intent, RequestCodes.REQUEST_MEAL_SETTINGS);
    }

    private void deleteMeal() {
        String mealName = mMeal.getName();
        Calendar cal = mMeal.getTime();
        if(mealName.equals("Untitled")) {
            if (cal.get(Calendar.HOUR_OF_DAY) > 4 && cal.get(Calendar.HOUR_OF_DAY) < 11)
                mealName = "Breakfast";
            else if (cal.get(Calendar.HOUR_OF_DAY) >= 11 && cal.get(Calendar.HOUR_OF_DAY) < 15)
                mealName = "Lunch";
            else if (cal.get(Calendar.HOUR_OF_DAY) >= 15 && cal.get(Calendar.HOUR_OF_DAY) < 18)
                mealName = "Snack";
            else if (cal.get(Calendar.HOUR_OF_DAY) >= 18 && cal.get(Calendar.HOUR_OF_DAY) < 22)
                mealName = "Dinner";
            else if(cal.get(Calendar.HOUR_OF_DAY) >= 22 || cal.get(Calendar.HOUR_OF_DAY) <= 4)
                mealName = "Night snack";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MealDetailsActivity.this);
        builder
                .setTitle("Are you sure?")
                .setMessage("You're going to delete your meal \"" + mealName + "\" and all its own data. This action cannot be undone.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        mFoodModule.removeMeal(mMeal, new FoodModule.OnVoidResultListener() {
                            @Override
                            public void onResult() {
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void addFood() {
        Intent intent = new Intent(MealDetailsActivity.this, FoodsListActivity.class);
        startActivityForResult(intent, RequestCodes.REQUEST_FOOD_LIST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meal_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_add_food:
                addFood();
                return true;
            case R.id.menu_meal_settings:
                openSettings();
                return true;
            case R.id.menu_delete_meal:
                deleteMeal();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateInfos() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String mealName = mMeal.getName();
                Calendar cal = mMeal.getTime();
                if(mealName.equals("Untitled")) {
                    if (cal.get(Calendar.HOUR_OF_DAY) > 4 && cal.get(Calendar.HOUR_OF_DAY) < 11)
                        mealName = "Breakfast";
                    else if (cal.get(Calendar.HOUR_OF_DAY) >= 11 && cal.get(Calendar.HOUR_OF_DAY) < 15)
                        mealName = "Lunch";
                    else if (cal.get(Calendar.HOUR_OF_DAY) >= 15 && cal.get(Calendar.HOUR_OF_DAY) < 18)
                        mealName = "Snack";
                    else if (cal.get(Calendar.HOUR_OF_DAY) >= 18 && cal.get(Calendar.HOUR_OF_DAY) < 22)
                        mealName = "Dinner";
                    else if(cal.get(Calendar.HOUR_OF_DAY) >= 22 || cal.get(Calendar.HOUR_OF_DAY) <= 4)
                        mealName = "Night snack";
                }
                mBinding.txtName.setText(mealName);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                mBinding.txtDate.setText(sdf.format(mMeal.getTime().getTime()));
            }
        });

        mFoodModule.getMealTotalCalories(mMeal, new FoodModule.OnDoubleResultListener() {
            @Override
            public void onResult(final double result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.txtTotalCal.setText(Common.DoubleStringify(result));
                    }
                });
            }
        });

        mFoodModule.getMealFoodsEatenList(mMeal, new FoodModule.OnFoodsEatenListResultListener() {
            @Override
            public void onResult(final ArrayList<FoodEaten> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FoodsOnMealAdapter adapter = new FoodsOnMealAdapter((UHealthApplication)getApplication(), MealDetailsActivity.this, result);
                        adapter.setOnFoodSettingsClick(MealDetailsActivity.this);
                        adapter.setOnFoodTrashClick(MealDetailsActivity.this);
                        mBinding.listFood.setAdapter(adapter);
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RequestCodes.REQUEST_MEAL_SETTINGS: {
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    updateInfos();

                    if (data != null) {
                        mMeal = data.getParcelableExtra(Extras.EXTRA_MEAL);
                        saveChanges();
                    }
                }
            }
            break;
            case RequestCodes.REQUEST_FOOD_LIST: {
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    updateInfos();

                    if (data != null) {
                        long id = data.getLongExtra(Extras.EXTRA_FOOD_ID, Long.MIN_VALUE);
                        if (id != Long.MIN_VALUE) {
                            Intent intent = new Intent(MealDetailsActivity.this, FoodEatenCreationActivity.class);
                            intent.putExtra(Extras.EXTRA_FOOD_ID, id);
                            startActivityForResult(intent, RequestCodes.REQUEST_FOOD_EATEN_CREATION);
                        }
                    }
                }
            }
            break;
            case RequestCodes.REQUEST_FOOD_EATEN_CREATION: {
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    updateInfos();

                    if (data != null) {
                        boolean updated = data.getBooleanExtra(Extras.EXTRA_FOOD_EATEN_UPDATED, false);
                        if(updated)
                            updateInfos();
                        else {
                            long id = data.getLongExtra(Extras.EXTRA_FOOD_EATEN_ID, Long.MIN_VALUE);
                            if (id != Long.MIN_VALUE) {
                                mMeal.getFoodsEaten().add(id);
                                saveChanges();
                            }
                        }
                    }
                }
            }
            break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        closeActivity();
        return true;
    }

    @Override
    public void onFoodSettingsClick(FoodEaten f) {
        Intent intent = new Intent(MealDetailsActivity.this, FoodEatenCreationActivity.class);
        intent.putExtra(Extras.EXTRA_FOOD_EATEN, (Parcelable)f);
        startActivityForResult(intent, RequestCodes.REQUEST_FOOD_EATEN_CREATION);
    }

    @Override
    public void onFoodTrashClick(final FoodEaten fe) {
        mFoodModule.getFoodsById(new FoodModule.OnFoodsListResultListener() {
            @Override
            public void onResult(ArrayList<Food> result) {
                if (result.size() > 0) {
                    final Food f = result.get(0);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MealDetailsActivity.this);
                            builder
                                    .setTitle("Are you sure?")
                                    .setMessage("You're going to delete from this meal your \"" + f.getName() + "\" (" + Common.DoubleStringify(fe.getQuantity()) + " grams) and all its own data. This action cannot be undone.")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            setResult(RESULT_OK);
                                            mMeal.getFoodsEaten().remove(fe.getId());
                                            saveChanges();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    });

                }
            }
        }, fe.getFoodId());
    }

    private void saveChanges() {
        mFoodModule.updateMeal(mMeal, null);
        updateInfos();
    }
}
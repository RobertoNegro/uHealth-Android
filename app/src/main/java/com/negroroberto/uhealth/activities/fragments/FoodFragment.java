package com.negroroberto.uhealth.activities.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.MealDetailsActivity;
import com.negroroberto.uhealth.activities.adapters.MealsAdapter;
import com.negroroberto.uhealth.activities.fragments.abstracts.ModuleFragment;
import com.negroroberto.uhealth.databinding.FragmentFoodBinding;
import com.negroroberto.uhealth.modules.food.FoodModule;
import com.negroroberto.uhealth.modules.food.models.Meal;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.Extras;
import com.negroroberto.uhealth.utils.RequestCodes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class FoodFragment extends ModuleFragment {
    private FragmentFoodBinding mBinding;
    private ArrayList<Meal> mMeals;
    private FoodModule mFoodModule;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getActivity() == null)
            return null;

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_food, container, false);
        mFoodModule = new FoodModule((UHealthApplication)getActivity().getApplication());

        updateData();

        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.listitem_plus_footer, mBinding.listMeals, false);
        mBinding.listMeals.addFooterView(footer, null, true);
        footer.findViewById(R.id.btnFooterPlus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Meal newMeal = new Meal();

                Calendar todayCal = Calendar.getInstance();
                todayCal.setTime(new Date());
                todayCal.set(Calendar.HOUR_OF_DAY, 0);
                todayCal.set(Calendar.MINUTE, 0);
                todayCal.set(Calendar.SECOND, 0);
                todayCal.set(Calendar.MILLISECOND, 0);

                Calendar actCal = Calendar.getInstance();
                actCal.setTime(new Date());
                actCal.set(Calendar.SECOND, 0);
                actCal.set(Calendar.MILLISECOND, 0);

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(getTimeStart()));
                cal.set(Calendar.HOUR_OF_DAY, 12);
                cal.set(Calendar.MINUTE, 30);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                if (cal.compareTo(todayCal) > 0)
                    cal = actCal;

                newMeal.setName("Untitled");
                newMeal.setTime(cal);

                mFoodModule.createMeal(new FoodModule.OnLongResultListener() {
                    @Override
                    public void onResult(long result) {
                        updateData();
                    }
                }, newMeal);
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void updateData() {
        if (mBinding == null || getActivity() == null)
            return;

        final float totalCalories = ((UHealthApplication) getActivity().getApplication()).getGoals().getFoodCalories();
        final float totalCarbs = ((UHealthApplication) getActivity().getApplication()).getGoals().getFoodCarbs();
        final float totalFat = ((UHealthApplication) getActivity().getApplication()).getGoals().getFoodFat();
        final float totalProtein = ((UHealthApplication) getActivity().getApplication()).getGoals().getFoodProtein();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (totalCalories <= 0)
                    mBinding.goalContainerCalories.setVisibility(View.INVISIBLE);
                else
                    mBinding.goalContainerCalories.setVisibility(View.VISIBLE);
                mBinding.txtTotalCalories.setText(Common.DoubleStringify(totalCalories));
                mBinding.circularCalories.setTotalValue(totalCalories);

                if (totalCarbs <= 0)
                    mBinding.goalContainerCarbs.setVisibility(View.INVISIBLE);
                else
                    mBinding.goalContainerCarbs.setVisibility(View.VISIBLE);
                mBinding.txtTotalCarbs.setText(Common.DoubleStringify(totalCarbs));
                mBinding.circularCarbs.setTotalValue(totalCarbs);

                if (totalFat <= 0)
                    mBinding.goalContainerFat.setVisibility(View.INVISIBLE);
                else
                    mBinding.goalContainerFat.setVisibility(View.VISIBLE);
                mBinding.txtTotalFat.setText(Common.DoubleStringify(totalFat));
                mBinding.circularFat.setTotalValue(totalFat);

                if (totalProtein <= 0)
                    mBinding.goalContainerProtein.setVisibility(View.INVISIBLE);
                else
                    mBinding.goalContainerProtein.setVisibility(View.VISIBLE);
                mBinding.txtTotalProtein.setText(Common.DoubleStringify(totalProtein));
                mBinding.circularProtein.setTotalValue(totalProtein);
            }
        });

        mFoodModule.getMealsByPeriod(getTimeStart(), getTimeEnd(), new FoodModule.OnMealsListResultListener() {
            @Override
            public void onResult(ArrayList<Meal> result) {
                mMeals = result;

                updateActualCals();
                updateActualCarbs();
                updateActualFat();
                updateActualProtein();

                final MealsAdapter adapter = new MealsAdapter(((UHealthApplication)getActivity().getApplication()),getActivity(), mMeals);
                adapter.setMealClickListener(new MealsAdapter.OnMealClickListener() {
                    @Override
                    public void onMealClick(Meal m) {
                        Intent intent = new Intent(getContext(), MealDetailsActivity.class);
                        intent.putExtra(Extras.EXTRA_MEAL, (Parcelable) m);
                        startActivityForResult(intent, RequestCodes.REQUEST_MEAL_DETAILS);
                    }
                });

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.listMeals.setAdapter(adapter);
                    }
                });
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RequestCodes.REQUEST_MEAL_DETAILS:
                if (resultCode == RESULT_OK)
                    updateData();
                break;
        }
    }

    private void updateActualCals() {
        updateActualCalsRec(0, 0);
    }

    private void updateActualCalsRec(final double sum, final int index) {
        if (mMeals == null || index >= mMeals.size()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.txtActualCalories.setText(Common.DoubleStringify(sum));
                    mBinding.circularCalories.setActualValue(sum);
                }
            });
        } else {
            mFoodModule.getMealTotalCalories(mMeals.get(index), new FoodModule.OnDoubleResultListener() {
                @Override
                public void onResult(double result) {
                    updateActualCalsRec(sum + result, index + 1);
                }
            });
        }
    }

    private void updateActualCarbs() {
        updateActualCarbsRec(0, 0);
    }

    private void updateActualCarbsRec(final double sum, final int index) {
        if (mMeals == null || index >= mMeals.size()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.txtActualCarbs.setText(Common.DoubleStringify(sum));
                    mBinding.circularCarbs.setActualValue(sum);
                }
            });
        } else {
            mFoodModule.getMealTotalCarbs(mMeals.get(index), new FoodModule.OnDoubleResultListener() {
                @Override
                public void onResult(double result) {
                    updateActualCarbsRec(sum + result, index + 1);
                }
            });
        }
    }

    private void updateActualFat() {
        updateActualFatRec(0, 0);
    }

    private void updateActualFatRec(final double sum, final int index) {
        if (mMeals == null || index >= mMeals.size()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.txtActualFat.setText(Common.DoubleStringify(sum));
                    mBinding.circularFat.setActualValue(sum);
                }
            });
        } else {
            mFoodModule.getMealTotalFat(mMeals.get(index), new FoodModule.OnDoubleResultListener() {
                @Override
                public void onResult(double result) {
                    updateActualFatRec(sum + result, index + 1);
                }
            });
        }
    }

    private void updateActualProtein() {
        updateActualProteinRec(0, 0);
    }

    private void updateActualProteinRec(final double sum, final int index) {
        if (mMeals == null || index >= mMeals.size()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.txtActualProtein.setText(Common.DoubleStringify(sum));
                    mBinding.circularProtein.setActualValue(sum);
                }
            });
        } else {
            mFoodModule.getMealTotalProtein(mMeals.get(index), new FoodModule.OnDoubleResultListener() {
                @Override
                public void onResult(double result) {
                    updateActualProteinRec(sum + result, index + 1);
                }
            });
        }
    }

}

package com.negroroberto.uhealth.activities.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.modules.food.FoodModule;
import com.negroroberto.uhealth.modules.food.models.Food;
import com.negroroberto.uhealth.modules.food.models.FoodEaten;
import com.negroroberto.uhealth.modules.food.models.Meal;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.Debug;
import com.negroroberto.uhealth.utils.FilePaths;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MealsAdapter extends ArrayAdapter<Meal> {
    private ArrayList<Meal> mMeals;
    private int mResource;
    private LayoutInflater mInflater;
    private FoodModule mFoodModule;
    private UHealthApplication mApplication;

    public MealsAdapter(UHealthApplication application, Activity activity, ArrayList<Meal> meals) {
        this(application, activity, meals, R.layout.listitem_meals);
    }

    private MealsAdapter(UHealthApplication application, Activity activity, ArrayList<Meal> meals, int resource) {
        super(activity, resource, meals);
        this.mApplication = application;
        this.mMeals = meals;
        this.mResource = resource;
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.mFoodModule = new FoodModule(mApplication);
    }

    private interface OnUpdateCalsFinish{
        void onFinish(double value);
    }
    private void getTotalCals(final OnUpdateCalsFinish listener) {
        getTotalCalsRec(0, 0, listener);
    }
    private void getTotalCalsRec(final double sum, final int index, final OnUpdateCalsFinish listener) {
        if(mMeals == null || index >= mMeals.size()) {
            if(listener != null)
                listener.onFinish(sum);
        }else {
            mFoodModule.getMealTotalCalories(mMeals.get(index), new FoodModule.OnDoubleResultListener() {
                @Override
                public void onResult(double result) {
                    getTotalCalsRec(sum+result, index+1, listener);
                }
            });
        }
    }

    private OnMealClickListener mMealClickListener;
    public interface OnMealClickListener {
        void onMealClick(Meal m);
    }
    public void setMealClickListener(OnMealClickListener mealClickListener) {
        mMealClickListener = mealClickListener;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.btnArrow = convertView.findViewById(R.id.btnArrow);
            holder.kcalBar = convertView.findViewById(R.id.kcalBar);
            holder.layoutPhotoContainer = convertView.findViewById(R.id.layoutPhotoContainer);
            holder.txtTime = convertView.findViewById(R.id.txtTime);
            holder.txtName = convertView.findViewById(R.id.txtName);
            holder.txtKcal = convertView.findViewById(R.id.txtKcal);
            holder.verticalBarTop = convertView.findViewById(R.id.verticalBarTop);
            holder.verticalBarBottom = convertView.findViewById(R.id.verticalBarBottom);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.btnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMealClickListener != null)
                    mMealClickListener.onMealClick(mMeals.get(position));
            }
        });

        String mealName = mMeals.get(position).getName();
        Calendar cal = mMeals.get(position).getTime();
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

        holder.txtName.setText(mealName);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.txtTime.setText(sdf.format(mMeals.get(position).getTime().getTime()));

        holder.layoutPhotoContainer.removeAllViews();

        mFoodModule.getMealFoodsEatenList(mMeals.get(position), new FoodModule.OnFoodsEatenListResultListener() {
            @Override
            public void onResult(ArrayList<FoodEaten> result) {
                ArrayList<Long> foodEatenIds = new ArrayList<>();
                for(FoodEaten fe : result)
                    foodEatenIds.add(fe.getFoodId());

                mFoodModule.getFoodsById(new FoodModule.OnFoodsListResultListener() {
                    @Override
                    public void onResult(ArrayList<Food> result) {
                        for (int i = 0; i < result.size(); i++) {
                            Food f = result.get(i);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Common.DpToPx(getContext(), 64), Common.DpToPx(getContext(), 64));
                            if (i == 0)
                                params.rightMargin = 0;
                            else
                                params.rightMargin = -Common.DpToPx(getContext(), 32);

                            final CircularImageView circularImageView = new CircularImageView(getContext(), null, R.style.CircularFoodPhoto);
                            circularImageView.setImageBitmap(Common.GetBitmapFromFile(getContext(),FilePaths.GetFoodImagePath(getContext()) + f.getPhoto()));
                            circularImageView.setLayoutParams(params);

                            ((Activity)getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.layoutPhotoContainer.addView(circularImageView);
                                }
                            });

                        }
                    }
                }, foodEatenIds.toArray(new Long[foodEatenIds.size()]));

                getTotalCals(new OnUpdateCalsFinish() {
                    @Override
                    public void onFinish(final double totalCals) {
                        mFoodModule.getMealTotalCalories(mMeals.get(position), new FoodModule.OnDoubleResultListener() {
                            @Override
                            public void onResult(final double foodCals) {
                                final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.kcalBar.getLayoutParams();
                                params.weight = 20f + (float) (foodCals / totalCals) * 80f;
                                Debug.Log(this, mMeals.get(position).getName() + " bar: " + params.weight);

                                ((Activity)getContext()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.txtKcal.setText(Common.DoubleStringify(foodCals));
                                        holder.kcalBar.setLayoutParams(params);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        if (position == 0)
            holder.verticalBarTop.setVisibility(View.INVISIBLE);
        else
            holder.verticalBarTop.setVisibility(View.VISIBLE);

        if (position == mMeals.size() - 1)
            holder.verticalBarBottom.setVisibility(View.INVISIBLE);
        else
            holder.verticalBarBottom.setVisibility(View.VISIBLE);

        return convertView;
    }

    private static class ViewHolder {
        ImageButton btnArrow;
        ImageView kcalBar;
        LinearLayout layoutPhotoContainer;
        TextView txtTime;
        TextView txtName;
        TextView txtKcal;
        View verticalBarTop;
        View verticalBarBottom;
    }
}
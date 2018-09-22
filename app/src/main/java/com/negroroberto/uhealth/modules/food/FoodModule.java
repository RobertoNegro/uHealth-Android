package com.negroroberto.uhealth.modules.food;

import android.content.Context;
import android.os.AsyncTask;

import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.modules.Module;
import com.negroroberto.uhealth.modules.food.models.Food;
import com.negroroberto.uhealth.modules.food.models.FoodEaten;
import com.negroroberto.uhealth.modules.food.models.Meal;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.Debug;

import java.util.ArrayList;


public class FoodModule extends Module {
    public FoodModule(UHealthApplication application) {
        super(application);
    }

    public void getMealListTotalCalories(final ArrayList<Meal> meals, final OnDoubleResultListener listener) {
        getMealListTotalCalories(meals, 0, 0, listener);
    }

    private void getMealListTotalCalories(final ArrayList<Meal> meals, final double sum, final int index, final OnDoubleResultListener listener) {
        if (meals == null || index >= meals.size())
            listener.onResult(sum);
        else {
            getMealTotalCalories(meals.get(index), new FoodModule.OnDoubleResultListener() {
                @Override
                public void onResult(double result) {
                    getMealListTotalCalories(meals, sum + result, index + 1, listener);
                }
            });
        }
    }

    public void getMealListTotalCarbs(final ArrayList<Meal> meals, final OnDoubleResultListener listener) {
        getMealListTotalCarbs(meals, 0, 0, listener);
    }

    private void getMealListTotalCarbs(final ArrayList<Meal> meals, final double sum, final int index, final OnDoubleResultListener listener) {
        if (meals == null || index >= meals.size())
            listener.onResult(sum);
        else {
            getMealTotalCarbs(meals.get(index), new FoodModule.OnDoubleResultListener() {
                @Override
                public void onResult(double result) {
                    getMealListTotalCarbs(meals, sum + result, index + 1, listener);
                }
            });
        }
    }

    public void getMealListTotalFat(final ArrayList<Meal> meals, final OnDoubleResultListener listener) {
        getMealListTotalFat(meals, 0, 0, listener);
    }

    private void getMealListTotalFat(final ArrayList<Meal> meals, final double sum, final int index, final OnDoubleResultListener listener) {
        if (meals == null || index >= meals.size())
            listener.onResult(sum);
        else {
            getMealTotalFat(meals.get(index), new FoodModule.OnDoubleResultListener() {
                @Override
                public void onResult(double result) {
                    getMealListTotalFat(meals, sum + result, index + 1, listener);
                }
            });
        }
    }

    public void getMealListTotalProtein(final ArrayList<Meal> meals, final OnDoubleResultListener listener) {
        getMealListTotalProtein(meals, 0, 0, listener);
    }

    private void getMealListTotalProtein(final ArrayList<Meal> meals, final double sum, final int index, final OnDoubleResultListener listener) {
        if (meals == null || index >= meals.size())
            listener.onResult(sum);
        else {
            getMealTotalProtein(meals.get(index), new FoodModule.OnDoubleResultListener() {
                @Override
                public void onResult(double result) {
                    getMealListTotalProtein(meals, sum + result, index + 1, listener);
                }
            });
        }
    }

    public void removeFood(final OnVoidResultListener listener, final Food food) {
        final long id = food.getId();
        GetFoodsEatenByFoodIdTask task = new GetFoodsEatenByFoodIdTask(getApplication(), id, new OnFoodsEatenListResultListener() {
            @Override
            public void onResult(ArrayList<FoodEaten> result) {
                DeleteFoodsEatenTask task = new DeleteFoodsEatenTask(getApplication(), new OnVoidResultListener() {
                    @Override
                    public void onResult() {
                        DeleteFoodsTask task = new DeleteFoodsTask(getApplication(), listener);
                        task.execute(food);
                    }
                });
                task.execute(result.toArray(new FoodEaten[result.size()]));
            }
        });
        task.execute();
    }

    public void updateFood(final OnVoidResultListener listener, final Food food) {
        UpdateFoodsTask task = new UpdateFoodsTask(getApplication(), listener);
        task.execute(food);
    }

    public void createFood(final OnLongResultListener listener, final Food food) {
        InsertFoodsTask task = new InsertFoodsTask(getApplication(), new OnLongArrayResultListener() {
            @Override
            public void onResult(long[] result) {
                if (result.length > 0)
                    listener.onResult(result[0]);
            }
        });
        task.execute(food);
    }

    public void createMeal(final OnLongResultListener listener, final Meal meal) {
        InsertMealsTask task = new InsertMealsTask(getApplication(), new OnLongArrayResultListener() {
            @Override
            public void onResult(long[] result) {
                if (result.length > 0)
                    listener.onResult(result[0]);
            }
        });
        task.execute(meal);
    }

    public void removeMeal(final Meal meal, final OnVoidResultListener listener) {
        DeleteMealsTask task = new DeleteMealsTask(getApplication(), listener);
        task.execute(meal);
    }

    public void updateMeal(final Meal meal, final OnVoidResultListener listener) {
        UpdateMealsTask task = new UpdateMealsTask(getApplication(), listener);
        task.execute(meal);
    }

    public void getAllFoodsList(final OnFoodsListResultListener listener) {
        GetAllFoodsTask task = new GetAllFoodsTask(getApplication(), listener);
        task.execute();
    }

    public void getMealTotalCalories(final long id, final OnDoubleResultListener listener) {
        getMealFoodsEatenList(id, new OnFoodsEatenListResultListener() {
            @Override
            public void onResult(ArrayList<FoodEaten> result) {
                getFoodsListTotalCalories(result, 0, 0, listener);
            }
        });
    }

    public void getMealTotalCalories(final Meal meal, final OnDoubleResultListener listener) {
        getMealFoodsEatenList(meal, new OnFoodsEatenListResultListener() {
            @Override
            public void onResult(ArrayList<FoodEaten> result) {
                getFoodsListTotalCalories(result, 0, 0, listener);
            }
        });
    }

    public void getFoodsListTotalCalories(final ArrayList<FoodEaten> foodsEaten, final int index, final double value, final OnDoubleResultListener listener) {
        if (index < 0 || index >= foodsEaten.size()) {
            if (listener != null)
                listener.onResult(value);
            return;
        }

        FoodEaten fe = foodsEaten.get(index);
        getFoodsById(new OnFoodsListResultListener() {
            @Override
            public void onResult(ArrayList<Food> result) {
                double newValue = value;
                if (result.size() > 0)
                    newValue += (result.get(0).getEnergy() / 100d * foodsEaten.get(index).getQuantity());

                getFoodsListTotalCalories(foodsEaten, index + 1, newValue, listener);
            }
        }, fe.getFoodId());
    }

    public void getMealTotalCarbs(final long id, final OnDoubleResultListener listener) {
        getMealFoodsEatenList(id, new OnFoodsEatenListResultListener() {
            @Override
            public void onResult(ArrayList<FoodEaten> result) {
                getFoodsListTotalCarbs(result, 0, 0, listener);
            }
        });
    }

    public void getMealTotalCarbs(final Meal meal, final OnDoubleResultListener listener) {
        getMealFoodsEatenList(meal, new OnFoodsEatenListResultListener() {
            @Override
            public void onResult(ArrayList<FoodEaten> result) {
                getFoodsListTotalCarbs(result, 0, 0, listener);
            }
        });
    }

    public void getFoodsListTotalCarbs(final ArrayList<FoodEaten> foodsEaten, final int index, final double value, final OnDoubleResultListener listener) {
        if (index < 0 || index >= foodsEaten.size()) {
            if (listener != null)
                listener.onResult(value);
            return;
        }

        FoodEaten fe = foodsEaten.get(index);
        getFoodsById(new OnFoodsListResultListener() {
            @Override
            public void onResult(ArrayList<Food> result) {
                double newValue = value;
                if (result.size() > 0)
                    newValue += (result.get(0).getCarbohydrates() / 100d * foodsEaten.get(index).getQuantity());
                getFoodsListTotalCarbs(foodsEaten, index + 1, newValue, listener);
            }
        }, fe.getFoodId());
    }

    public void getMealTotalFat(final long id, final OnDoubleResultListener listener) {
        getMealFoodsEatenList(id, new OnFoodsEatenListResultListener() {
            @Override
            public void onResult(ArrayList<FoodEaten> result) {
                getFoodsListTotalFat(result, 0, 0, listener);
            }
        });
    }

    public void getMealTotalFat(final Meal meal, final OnDoubleResultListener listener) {
        getMealFoodsEatenList(meal, new OnFoodsEatenListResultListener() {
            @Override
            public void onResult(ArrayList<FoodEaten> result) {
                getFoodsListTotalFat(result, 0, 0, listener);
            }
        });
    }

    public void getFoodsListTotalFat(final ArrayList<FoodEaten> foodsEaten, final int index, final double value, final OnDoubleResultListener listener) {
        if (index < 0 || index >= foodsEaten.size()) {
            if (listener != null)
                listener.onResult(value);
            return;
        }

        FoodEaten fe = foodsEaten.get(index);
        getFoodsById(new OnFoodsListResultListener() {
            @Override
            public void onResult(ArrayList<Food> result) {
                double newValue = value;
                if (result.size() > 0)
                    newValue += (result.get(0).getFat() / 100d * foodsEaten.get(index).getQuantity());
                getFoodsListTotalFat(foodsEaten, index + 1, newValue, listener);
            }
        }, fe.getFoodId());
    }

    public void getMealTotalProtein(final long id, final OnDoubleResultListener listener) {
        getMealFoodsEatenList(id, new OnFoodsEatenListResultListener() {
            @Override
            public void onResult(ArrayList<FoodEaten> result) {
                getFoodsListTotalProtein(result, 0, 0, listener);
            }
        });
    }

    public void getMealTotalProtein(final Meal meal, final OnDoubleResultListener listener) {
        getMealFoodsEatenList(meal, new OnFoodsEatenListResultListener() {
            @Override
            public void onResult(ArrayList<FoodEaten> result) {
                getFoodsListTotalProtein(result, 0, 0, listener);
            }
        });
    }

    public void getFoodsListTotalProtein(final ArrayList<FoodEaten> foodsEaten, final int index, final double value, final OnDoubleResultListener listener) {
        if (index < 0 || index >= foodsEaten.size()) {
            if (listener != null)
                listener.onResult(value);
            return;
        }

        FoodEaten fe = foodsEaten.get(index);
        getFoodsById(new OnFoodsListResultListener() {
            @Override
            public void onResult(ArrayList<Food> result) {
                double newValue = value;
                if (result.size() > 0)
                    newValue += (result.get(0).getProteins() / 100d * foodsEaten.get(index).getQuantity());
                getFoodsListTotalProtein(foodsEaten, index + 1, newValue, listener);
            }
        }, fe.getFoodId());
    }

    public void getMealTotalFibers(final long id, final OnDoubleResultListener listener) {
        getMealFoodsEatenList(id, new OnFoodsEatenListResultListener() {
            @Override
            public void onResult(ArrayList<FoodEaten> result) {
                getFoodsListTotalFibers(result, 0, 0, listener);
            }
        });
    }

    public void getMealTotalFibers(final Meal meal, final OnDoubleResultListener listener) {
        getMealFoodsEatenList(meal, new OnFoodsEatenListResultListener() {
            @Override
            public void onResult(ArrayList<FoodEaten> result) {
                getFoodsListTotalFibers(result, 0, 0, listener);
            }
        });
    }

    public void getFoodsListTotalFibers(final ArrayList<FoodEaten> foodsEaten, final int index, final double value, final OnDoubleResultListener listener) {
        if (index < 0 || index >= foodsEaten.size()) {
            if (listener != null)
                listener.onResult(value);
            return;
        }

        FoodEaten fe = foodsEaten.get(index);
        getFoodsById(new OnFoodsListResultListener() {
            @Override
            public void onResult(ArrayList<Food> result) {
                double newValue = value;
                if (result.size() > 0)
                    newValue += (result.get(0).getFiber() / 100d * foodsEaten.get(index).getQuantity());
                getFoodsListTotalFibers(foodsEaten, index + 1, newValue, listener);
            }
        }, fe.getFoodId());
    }

    public void getMealFoodsEatenList(final long id, final OnFoodsEatenListResultListener listener) {
        getMealsById(new OnMealsListResultListener() {
            @Override
            public void onResult(ArrayList<Meal> result) {
                if (result.size() > 0)
                    getMealFoodsEatenList(result.get(0), listener);
            }
        }, id);
    }

    public void getMealFoodsEatenList(final Meal meal, final OnFoodsEatenListResultListener listener) {
        Long[] foodsEatenId = meal.getFoodsEaten().toArray(new Long[meal.getFoodsEaten().size()]);
        getFoodsEatenById(new OnFoodsEatenListResultListener() {
            @Override
            public void onResult(ArrayList<FoodEaten> result) {
                if (listener != null)
                    listener.onResult(result);
            }
        }, foodsEatenId);
    }

    public void getMealsByPeriod(final long startTime, final long endTime, final OnMealsListResultListener listener) {
        GetMealsByPeriodTask task = new GetMealsByPeriodTask(getApplication(), startTime, endTime, listener);
        task.execute();
    }

    public void getMealsById(final OnMealsListResultListener listener, final Long... ids) {
        GetMealsByIdTask task = new GetMealsByIdTask(getApplication(), listener);
        task.execute(ids);
    }

    public void getFoodsEatenById(final OnFoodsEatenListResultListener listener, final Long... ids) {
        GetFoodsEatenByIdTask task = new GetFoodsEatenByIdTask(getApplication(), listener);
        task.execute(ids);
    }

    public void getFoodsById(final OnFoodsListResultListener listener, final Long... ids) {
        GetFoodsByIdTask task = new GetFoodsByIdTask(getApplication(), listener);
        task.execute(ids);
    }

    public void removeFoodEaten(final OnVoidResultListener listener, final FoodEaten foodEaten) {
        DeleteFoodsEatenTask task = new DeleteFoodsEatenTask(getApplication(), listener);
        task.execute(foodEaten);
    }

    public void updateFoodEaten(final OnVoidResultListener listener, final FoodEaten foodEaten) {
        UpdateFoodsEatenTask task = new UpdateFoodsEatenTask(getApplication(), listener);
        task.execute(foodEaten);
    }

    public void createFoodEaten(final OnLongResultListener listener, final FoodEaten foodEaten) {
        InsertFoodsEatenTask task = new InsertFoodsEatenTask(getApplication(), new OnLongArrayResultListener() {
            @Override
            public void onResult(long[] result) {
                if (result.length > 0)
                    listener.onResult(result[0]);
            }
        });
        task.execute(foodEaten);
    }

    //region AsyncTasks
    private static class InsertFoodsTask extends AsyncTask<Food, Void, Void> {
        private OnLongArrayResultListener mListener;
        private UHealthApplication mApplication;

        public InsertFoodsTask(UHealthApplication application, OnLongArrayResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Food... foods) {
            long[] ids = mApplication.getDatabaseInstance().foodDao().insertAll(foods);

            if (mListener != null)
                mListener.onResult(ids);
            return null;
        }
    }

    private static class DeleteFoodsTask extends AsyncTask<Food, Void, Void> {
        private OnVoidResultListener mListener;
        private UHealthApplication mApplication;

        public DeleteFoodsTask(UHealthApplication application, OnVoidResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Food... foods) {
            for (Food f : foods)
                mApplication.getDatabaseInstance().foodDao().delete(f);

            if (mListener != null)
                mListener.onResult();
            return null;
        }
    }

    private static class UpdateFoodsTask extends AsyncTask<Food, Void, Void> {
        private OnVoidResultListener mListener;
        private UHealthApplication mApplication;

        public UpdateFoodsTask(UHealthApplication application, OnVoidResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Food... foods) {
            for (Food f : foods)
                mApplication.getDatabaseInstance().foodDao().update(f);

            if (mListener != null)
                mListener.onResult();
            return null;
        }
    }

    private static class GetFoodsByIdTask extends AsyncTask<Long, Void, Void> {
        private OnFoodsListResultListener mListener;
        private UHealthApplication mApplication;

        public GetFoodsByIdTask(UHealthApplication application, OnFoodsListResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Long... ids) {
            ArrayList<Food> foods = new ArrayList<>();
            for (final long id : ids) {
                Food f = mApplication.getDatabaseInstance().foodDao().findById(id);
                if (f != null)
                    foods.add(f);
            }

            if (mListener != null)
                mListener.onResult(foods);
            return null;
        }
    }

    private static class GetAllFoodsTask extends AsyncTask<Void, Void, Void> {
        private OnFoodsListResultListener mListener;
        private UHealthApplication mApplication;

        public GetAllFoodsTask(UHealthApplication application, OnFoodsListResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<Food> foods = new ArrayList<>(mApplication.getDatabaseInstance().foodDao().getAll());
            if (mListener != null)
                mListener.onResult(foods);
            return null;
        }
    }


    private static class InsertFoodsEatenTask extends AsyncTask<FoodEaten, Void, Void> {
        private OnLongArrayResultListener mListener;
        private UHealthApplication mApplication;

        public InsertFoodsEatenTask(UHealthApplication application, OnLongArrayResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(FoodEaten... foodEatens) {
            long[] ids = mApplication.getDatabaseInstance().foodEatenDao().insertAll(foodEatens);

            if (mListener != null)
                mListener.onResult(ids);
            return null;
        }
    }

    private static class DeleteFoodsEatenTask extends AsyncTask<FoodEaten, Void, Void> {
        private OnVoidResultListener mListener;
        private UHealthApplication mApplication;

        public DeleteFoodsEatenTask(UHealthApplication application, OnVoidResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(FoodEaten... foodEatens) {
            for (FoodEaten fe : foodEatens)
                mApplication.getDatabaseInstance().foodEatenDao().delete(fe);

            if (mListener != null)
                mListener.onResult();
            return null;
        }
    }

    private static class UpdateFoodsEatenTask extends AsyncTask<FoodEaten, Void, Void> {
        private OnVoidResultListener mListener;
        private UHealthApplication mApplication;

        public UpdateFoodsEatenTask(UHealthApplication application, OnVoidResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(FoodEaten... foodEatens) {
            for (FoodEaten fe : foodEatens)
                mApplication.getDatabaseInstance().foodEatenDao().update(fe);

            if (mListener != null)
                mListener.onResult();
            return null;
        }
    }

    private static class GetFoodsEatenByIdTask extends AsyncTask<Long, Void, Void> {
        private OnFoodsEatenListResultListener mListener;
        private UHealthApplication mApplication;

        public GetFoodsEatenByIdTask(UHealthApplication application, OnFoodsEatenListResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Long... ids) {
            ArrayList<FoodEaten> foodsEaten = new ArrayList<>();
            for (final long id : ids) {
                FoodEaten fe = mApplication.getDatabaseInstance().foodEatenDao().findById(id);
                if (fe != null)
                    foodsEaten.add(fe);
            }

            if (mListener != null)
                mListener.onResult(foodsEaten);
            return null;
        }
    }

    private static class GetFoodsEatenByFoodIdTask extends AsyncTask<Void, Void, Void> {
        private OnFoodsEatenListResultListener mListener;
        private UHealthApplication mApplication;
        private long mFoodId;

        public GetFoodsEatenByFoodIdTask(UHealthApplication application, long foodId, OnFoodsEatenListResultListener listener) {
            mApplication = application;
            mListener = listener;
            mFoodId = foodId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<FoodEaten> foodsEaten = new ArrayList<>(mApplication.getDatabaseInstance().foodEatenDao().findByFoodId(mFoodId));
            if (mListener != null)
                mListener.onResult(foodsEaten);
            return null;
        }
    }


    private static class InsertMealsTask extends AsyncTask<Meal, Void, Void> {
        private OnLongArrayResultListener mListener;
        private UHealthApplication mApplication;

        public InsertMealsTask(UHealthApplication application, OnLongArrayResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Meal... meals) {
            long[] ids = mApplication.getDatabaseInstance().mealDao().insertAll(meals);

            if (mListener != null)
                mListener.onResult(ids);
            return null;
        }
    }

    private static class DeleteMealsTask extends AsyncTask<Meal, Void, Void> {
        private OnVoidResultListener mListener;
        private UHealthApplication mApplication;

        public DeleteMealsTask(UHealthApplication application, OnVoidResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Meal... meals) {
            for (Meal m : meals)
                mApplication.getDatabaseInstance().mealDao().delete(m);

            if (mListener != null)
                mListener.onResult();
            return null;
        }
    }

    private static class UpdateMealsTask extends AsyncTask<Meal, Void, Void> {
        private OnVoidResultListener mListener;
        private UHealthApplication mApplication;

        public UpdateMealsTask(UHealthApplication application, OnVoidResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Meal... meals) {
            for (Meal m : meals)
                mApplication.getDatabaseInstance().mealDao().update(m);

            if (mListener != null)
                mListener.onResult();
            return null;
        }
    }

    private static class GetMealsByIdTask extends AsyncTask<Long, Void, Void> {
        private OnMealsListResultListener mListener;
        private UHealthApplication mApplication;

        public GetMealsByIdTask(UHealthApplication application, OnMealsListResultListener listener) {
            mApplication = application;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Long... ids) {
            ArrayList<Meal> meals = new ArrayList<>();
            for (final long id : ids) {
                Meal m = mApplication.getDatabaseInstance().mealDao().findById(id);
                if (m != null)
                    meals.add(m);
            }

            if (mListener != null)
                mListener.onResult(meals);
            return null;
        }
    }

    private static class GetMealsByPeriodTask extends AsyncTask<Void, Void, Void> {
        private long mStartTime;
        private long mEndTime;
        private OnMealsListResultListener mListener;
        private UHealthApplication mApplication;

        public GetMealsByPeriodTask(UHealthApplication application, long startTime, long endTime, OnMealsListResultListener listener) {
            mApplication = application;
            mStartTime = startTime;
            mEndTime = endTime;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<Meal> meals = new ArrayList<>(mApplication.getDatabaseInstance().mealDao().findByPeriod(mStartTime, mEndTime));
            if (mListener != null)
                mListener.onResult(meals);
            return null;
        }
    }

    //endregion

    //region listeners
    public interface OnVoidResultListener {
        void onResult();
    }

    public interface OnMealResultListener {
        void onResult(Meal result);
    }

    public interface OnMealsListResultListener {
        void onResult(ArrayList<Meal> result);
    }

    public interface OnFoodResultListener {
        void onResult(Food result);
    }

    public interface OnFoodsListResultListener {
        void onResult(ArrayList<Food> result);
    }

    public interface OnFoodEatenResultListener {
        void onResult(FoodEaten result);
    }

    public interface OnFoodsEatenListResultListener {
        void onResult(ArrayList<FoodEaten> result);
    }

    public interface OnLongArrayResultListener {
        void onResult(long[] result);
    }

    public interface OnLongResultListener {
        void onResult(long result);
    }

    public interface OnDoubleResultListener {
        void onResult(double result);
    }
    //endregion
}

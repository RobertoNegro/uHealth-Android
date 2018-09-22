package com.negroroberto.uhealth.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.negroroberto.uhealth.modules.food.dao.FoodDao;
import com.negroroberto.uhealth.modules.food.dao.FoodEatenDao;
import com.negroroberto.uhealth.modules.food.dao.MealDao;
import com.negroroberto.uhealth.modules.food.dao.WaterDrunkDao;
import com.negroroberto.uhealth.modules.food.models.Food;
import com.negroroberto.uhealth.modules.food.models.FoodEaten;
import com.negroroberto.uhealth.modules.food.models.Meal;
import com.negroroberto.uhealth.modules.water.models.WaterDrunk;

@Database(entities = {Food.class, FoodEaten.class, Meal.class, WaterDrunk.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class UHealthDatabase extends RoomDatabase {
    public static final String NAME = "UHEALTH_DATABASE";
    public abstract FoodDao foodDao();
    public abstract FoodEatenDao foodEatenDao();
    public abstract MealDao mealDao();
    public abstract WaterDrunkDao waterDrunkDao();
}

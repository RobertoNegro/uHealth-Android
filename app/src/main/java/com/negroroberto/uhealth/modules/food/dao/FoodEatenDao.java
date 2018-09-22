package com.negroroberto.uhealth.modules.food.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.negroroberto.uhealth.modules.food.models.FoodEaten;

import java.util.List;

@Dao
public interface FoodEatenDao {
    @Query("SELECT * FROM foodeaten ORDER BY mid")
    List<FoodEaten> getAll();

    @Query("SELECT * FROM foodeaten WHERE mid LIKE :id")
    FoodEaten findById(long id);

    @Query("SELECT * FROM foodeaten WHERE food_id LIKE :id")
    List<FoodEaten> findByFoodId(long id);

    @Insert
    long[] insertAll(FoodEaten... foodsEaten);

    @Update
    void update(FoodEaten foodEaten);

    @Delete
    void delete(FoodEaten foodEaten);
}

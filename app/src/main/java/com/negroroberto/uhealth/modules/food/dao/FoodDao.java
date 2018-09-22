package com.negroroberto.uhealth.modules.food.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.negroroberto.uhealth.modules.food.models.Food;

import java.util.List;

@Dao
public interface FoodDao {
    @Query("SELECT * FROM food ORDER BY mid")
    List<Food> getAll();

    @Query("SELECT * FROM food WHERE mid LIKE :id")
    Food findById(long id);

    @Query("SELECT * FROM food WHERE code LIKE :code LIMIT 1")
    Food findByCode(String code);

    @Insert
    long[] insertAll(Food... foods);

    @Update
    void update(Food food);

    @Delete
    void delete(Food food);

}

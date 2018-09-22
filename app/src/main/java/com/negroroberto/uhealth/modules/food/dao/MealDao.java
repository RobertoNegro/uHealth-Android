package com.negroroberto.uhealth.modules.food.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.negroroberto.uhealth.modules.food.models.Meal;

import java.util.List;

@Dao
public interface MealDao {
    @Query("SELECT * FROM meal")
    List<Meal> getAll();

    @Query("SELECT * FROM meal WHERE time BETWEEN :from AND :to ORDER BY time")
    List<Meal> findByPeriod(long from, long to);

    @Query("SELECT * FROM meal WHERE mid LIKE :id")
    Meal findById(long id);

    @Insert
    long[] insertAll(Meal... meals);

    @Update
    void update(Meal food);

    @Delete
    void delete(Meal meal);

}

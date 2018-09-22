package com.negroroberto.uhealth.modules.food.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.negroroberto.uhealth.modules.water.models.WaterDrunk;

import java.util.List;

@Dao
public interface WaterDrunkDao {
    @Query("SELECT * FROM waterdrunk")
    List<WaterDrunk> getAll();

    @Query("SELECT * FROM waterdrunk WHERE time BETWEEN :from AND :to ORDER BY time")
    List<WaterDrunk> findByPeriod(long from, long to);

    @Query("SELECT * FROM waterdrunk WHERE mid LIKE :id")
    WaterDrunk findById(long id);

    @Insert
    long[] insertAll(WaterDrunk... meals);

    @Update
    void update(WaterDrunk food);

    @Delete
    void delete(WaterDrunk meal);
}

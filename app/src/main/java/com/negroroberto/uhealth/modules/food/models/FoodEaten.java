package com.negroroberto.uhealth.modules.food.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

@Entity
public class FoodEaten implements Parcelable, Serializable {
    @PrimaryKey(autoGenerate = true)
    private long mId;

    @ColumnInfo(name = "food_id")
    private long mFoodId;

    @ColumnInfo(name = "quantity")
    private double mQuantity;

    public static final Creator<FoodEaten> CREATOR = new Creator<FoodEaten>() {
        @Override
        public FoodEaten createFromParcel(Parcel in) {
            return new FoodEaten(in);
        }

        @Override
        public FoodEaten[] newArray(int size) {
            return new FoodEaten[size];
        }
    };

    public FoodEaten() {
    }

    @Ignore
    public FoodEaten(Parcel p) {
        mId = p.readLong();
        mFoodId = p.readLong();
        mQuantity = p.readDouble();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getFoodId() {
        return mFoodId;
    }

    public void setFoodId(long foodId) {
        mFoodId = foodId;
    }

    public double getQuantity() {
        return mQuantity;
    }

    public void setQuantity(double quantity) {
        mQuantity = quantity;
    }

    @Override
    public String toString() {
        return "FoodEaten{" +
                "mId=" + mId +
                ", mFoodId=" + mFoodId +
                ", mQuantity=" + mQuantity +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mFoodId);
        dest.writeDouble(mQuantity);
    }
}

package com.negroroberto.uhealth.modules.food.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

@Entity
public class Meal implements Parcelable, Serializable {
    @PrimaryKey(autoGenerate = true)
    private long mId;

    @ColumnInfo(name = "foods")
    private ArrayList<Long> mFoodsEaten;
    @ColumnInfo(name = "time")
    private Calendar mTime;
    @ColumnInfo(name = "name")
    private String mName;

    public static final Creator<Meal> CREATOR = new Creator<Meal>() {
        @Override
        public Meal createFromParcel(Parcel in) {
            return new Meal(in);
        }

        @Override
        public Meal[] newArray(int size) {
            return new Meal[size];
        }
    };

    @Ignore
    public Meal(Parcel p) {
        mId = p.readLong();
        mFoodsEaten = p.readArrayList(Long.class.getClassLoader());
        mTime = (Calendar)p.readSerializable();
        mName = p.readString();
    }

    public Meal() {
        mFoodsEaten = new ArrayList<>();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public ArrayList<Long> getFoodsEaten() {
        return mFoodsEaten;
    }

    public void setFoodsEaten(ArrayList<Long> foodsEaten) {
        mFoodsEaten = foodsEaten;
    }

    public Calendar getTime() {
        return mTime;
    }

    public void setTime(Calendar time) {
        mTime = time;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeList(mFoodsEaten);
        dest.writeSerializable(mTime);
        dest.writeString(mName);
    }
}

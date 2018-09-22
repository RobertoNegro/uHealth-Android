package com.negroroberto.uhealth.modules.food.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

@Entity
public class Food implements Parcelable, Serializable {
    @PrimaryKey(autoGenerate = true)
    private long mId;

    @ColumnInfo(name = "code")
    private String mCode;

    @ColumnInfo(name = "brand")
    private String mBrand;
    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "photo")
    private String mPhoto;

    @ColumnInfo(name = "serving_quantity")
    private double mServingQuantity;

    @ColumnInfo(name = "energy")
    private double mEnergy;
    @ColumnInfo(name = "fat")
    private double mFat;
    @ColumnInfo(name = "carbohydrates")
    private double mCarbohydrates;
    @ColumnInfo(name = "fiber")
    private double mFiber;
    @ColumnInfo(name = "proteins")
    private double mProteins;
    @ColumnInfo(name = "salt")
    private double mSalt;
    @ColumnInfo(name = "sugars")
    private double mSugars;
    @ColumnInfo(name = "sodium")
    private double mSodium;

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    public Food() {
    }

    @Ignore
    public Food(Parcel p) {
        mId = p.readLong();
        mCode = p.readString();
        mBrand = p.readString();
        mName = p.readString();
        mPhoto = p.readString();
        mServingQuantity = p.readDouble();
        mEnergy = p.readDouble();
        mFat = p.readDouble();
        mCarbohydrates = p.readDouble();
        mFiber = p.readDouble();
        mProteins = p.readDouble();
        mSalt = p.readDouble();
        mSugars = p.readDouble();
        mSodium = p.readDouble();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        if (code == null)
            this.mCode = "N.A.";
        else
            this.mCode = code;
    }

    public String getBrand() {
        return mBrand;
    }

    public void setBrand(String brand) {
        if (brand == null)
            this.mBrand = "N.A.";
        else
            this.mBrand = brand;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        if (name == null)
            this.mName = "N.A.";
        else
            this.mName = name;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        this.mPhoto = photo;
    }

    public double getServingQuantity() {
        return mServingQuantity;
    }

    public void setServingQuantity(Double servingQuantity) {
        if (servingQuantity == null || servingQuantity < 0)
            this.mServingQuantity = 0;
        else
            this.mServingQuantity = servingQuantity;
    }

    public double getEnergy() {
        return mEnergy;
    }

    public void setEnergy(Double energy) {
        if (energy == null || energy < 0)
            this.mEnergy = 0;
        else
            this.mEnergy = energy;
    }

    public double getFat() {
        return mFat;
    }

    public void setFat(Double fat) {
        if (fat == null || fat < 0)
            this.mFat = 0;
        else
            this.mFat = fat;
    }

    public double getCarbohydrates() {
        return mCarbohydrates;
    }

    public void setCarbohydrates(Double carbohydrates) {
        if (carbohydrates == null || carbohydrates < 0)
            this.mCarbohydrates = 0;
        else
            this.mCarbohydrates = carbohydrates;
    }

    public double getFiber() {
        return mFiber;
    }

    public void setFiber(Double fiber) {
        if (fiber == null || fiber < 0)
            this.mFiber = 0;
        else
            this.mFiber = fiber;
    }

    public double getProteins() {
        return mProteins;
    }

    public void setProteins(Double proteins) {
        if (proteins == null || proteins < 0)
            this.mProteins = 0;
        else
            this.mProteins = proteins;
    }

    public double getSalt() {
        return mSalt;
    }

    public void setSalt(Double salt) {
        if (salt == null || salt < 0)
            this.mSalt = 0;
        else
            this.mSalt = salt;
    }

    public double getSugars() {
        return mSugars;
    }

    public void setSugars(Double sugars) {
        if (sugars == null || sugars < 0)
            this.mSugars = 0;
        else
            this.mSugars = sugars;
    }

    public double getSodium() {
        return mSodium;
    }

    public void setSodium(Double sodium) {
        if (sodium == null || sodium < 0)
            this.mSodium = 0;
        else
            this.mSodium = sodium;
    }

    @Override
    public String toString() {
        return "Food{" +
                "mCode='" + mCode + '\'' +
                ", mBrand='" + mBrand + '\'' +
                ", mName='" + mName + '\'' +
                ", mPhoto=" + (mPhoto != null ? mPhoto.toString() : "null") +
                ", mServingQuantity=" + mServingQuantity +
                ", mEnergy=" + mEnergy +
                ", mFat=" + mFat +
                ", mCarbohydrates=" + mCarbohydrates +
                ", mFiber=" + mFiber +
                ", mProteins=" + mProteins +
                ", mSalt=" + mSalt +
                ", mSugars=" + mSugars +
                ", mSodium=" + mSodium +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mCode);
        dest.writeString(mBrand);
        dest.writeString(mName);
        dest.writeString(mPhoto);
        dest.writeDouble(mServingQuantity);
        dest.writeDouble(mEnergy);
        dest.writeDouble(mFat);
        dest.writeDouble(mCarbohydrates);
        dest.writeDouble(mFiber);
        dest.writeDouble(mProteins);
        dest.writeDouble(mSalt);
        dest.writeDouble(mSugars);
        dest.writeDouble(mSodium);
    }
}

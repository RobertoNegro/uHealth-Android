package com.negroroberto.uhealth.modules.water.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.negroroberto.uhealth.utils.Debug;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity
public class WaterDrunk implements Parcelable, Serializable {
    @PrimaryKey(autoGenerate = true)
    private long mId;

    @ColumnInfo(name = "quantity")
    private long mQuantity;

    @ColumnInfo(name = "time")
    private Calendar mTime;

    public long getId() {
        return mId;
    }

    public void setId(Long id) {
        if (id == null)
            Debug.Warn(this, "Setting null id");
        else
            mId = id;
    }

    public long getQuantity() {
        return mQuantity;
    }

    public void setQuantity(Long quantity) {
        if (quantity == null || quantity < 0)
            mQuantity = 0;
        else
            mQuantity = quantity;
    }

    public Calendar getTime() {
        return mTime;
    }

    public void setTime(Calendar time) {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        if (time == null || time.compareTo(now) > 0)
            mTime = now;
        else
            mTime = time;
    }

    public static final Creator<WaterDrunk> CREATOR = new Creator<WaterDrunk>() {
        @Override
        public WaterDrunk createFromParcel(Parcel in) {
            return new WaterDrunk(in);
        }

        @Override
        public WaterDrunk[] newArray(int size) {
            return new WaterDrunk[size];
        }
    };

    public WaterDrunk() {
    }

    @Ignore
    public WaterDrunk(Parcel p) {
        mId = p.readLong();
        mQuantity = p.readLong();
        mTime = (Calendar) p.readSerializable();
    }

    @Override
    public String toString() {
        return "WaterDrunk{" +
                "mId=" + mId +
                ", mQuantity=" + mQuantity +
                ", mTime=" + mTime +
                '}';
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mQuantity);
        dest.writeSerializable(mTime);
    }
}

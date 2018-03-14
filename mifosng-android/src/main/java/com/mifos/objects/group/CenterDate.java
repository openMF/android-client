package com.mifos.objects.group;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by mayankjindal on 11/07/17.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class CenterDate extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    @SerializedName("centerId")
    long centerId;

    @PrimaryKey
    @SerializedName("chargeId")
    long chargeId;

    @Column
    @SerializedName("day")
    int day;

    @Column
    @SerializedName("month")
    int month;

    @Column
    @SerializedName("year")
    int year;

    public CenterDate(long centerId, long chargeId, int day, int month, int year) {
        this.centerId = centerId;
        this.chargeId = chargeId;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public long getCenterId() {
        return centerId;
    }

    public void setCenterId(long centerId) {
        this.centerId = centerId;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.centerId);
        dest.writeInt(this.day);
        dest.writeInt(this.month);
        dest.writeInt(this.year);
    }

    public CenterDate() {
    }

    protected CenterDate(Parcel in) {
        this.centerId = in.readLong();
        this.day = in.readInt();
        this.month = in.readInt();
        this.year = in.readInt();
    }

    public static final Parcelable.Creator<CenterDate> CREATOR = new Parcelable
            .Creator<CenterDate>() {
        @Override
        public CenterDate createFromParcel(Parcel source) {
            return new CenterDate(source);
        }

        @Override
        public CenterDate[] newArray(int size) {
            return new CenterDate[size];
        }
    };
}

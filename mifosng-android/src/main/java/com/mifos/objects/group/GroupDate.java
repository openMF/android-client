package com.mifos.objects.group;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by Rajan Maurya on 18/09/16.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class GroupDate extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    long groupId;

    @PrimaryKey
    long chargeId;

    @Column
    int day;

    @Column
    int month;

    @Column
    int year;

    public GroupDate(long groupId, long chargeId, int day, int month, int year) {
        this.groupId = groupId;
        this.chargeId = chargeId;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getChargeId() {
        return chargeId;
    }

    public void setChargeId(long chargeId) {
        this.chargeId = chargeId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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

    public GroupDate() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.groupId);
        dest.writeLong(this.chargeId);
        dest.writeInt(this.day);
        dest.writeInt(this.month);
        dest.writeInt(this.year);
    }

    protected GroupDate(Parcel in) {
        this.groupId = in.readLong();
        this.chargeId = in.readLong();
        this.day = in.readInt();
        this.month = in.readInt();
        this.year = in.readInt();
    }

    public static final Parcelable.Creator<GroupDate> CREATOR =
            new Parcelable.Creator<GroupDate>() {
        @Override
        public GroupDate createFromParcel(Parcel source) {
            return new GroupDate(source);
        }

        @Override
        public GroupDate[] newArray(int size) {
            return new GroupDate[size];
        }
    };
}

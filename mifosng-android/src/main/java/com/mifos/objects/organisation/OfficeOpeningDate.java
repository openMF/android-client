package com.mifos.objects.organisation;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by Rajan Maurya on 7/7/16.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class OfficeOpeningDate extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    int officeId;

    @Column
    int year;

    @Column
    int month;

    @Column
    int day;

    public int getOfficeId() {
        return this.officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "OfficeOpeningDate{" +
                "officeId=" + officeId +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.officeId);
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
    }

    public OfficeOpeningDate() {
    }

    protected OfficeOpeningDate(Parcel in) {
        this.officeId = in.readInt();
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
    }

    public static final Parcelable.Creator<OfficeOpeningDate> CREATOR = new Parcelable
            .Creator<OfficeOpeningDate>() {
        @Override
        public OfficeOpeningDate createFromParcel(Parcel source) {
            return new OfficeOpeningDate(source);
        }

        @Override
        public OfficeOpeningDate[] newArray(int size) {
            return new OfficeOpeningDate[size];
        }
    };
}

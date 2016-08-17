package com.mifos.objects.accounts.savings;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 *
 * Created by Rajan Maurya on 17/08/16.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class SavingsTransactionDate extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    Integer transactionId;

    @Column
    Integer year;

    @Column
    Integer month;

    @Column
    Integer day;

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.transactionId);
        dest.writeValue(this.year);
        dest.writeValue(this.month);
        dest.writeValue(this.day);
    }

    public SavingsTransactionDate() {
    }

    public SavingsTransactionDate(Integer transactionId, Integer year, Integer month, Integer day) {
        this.transactionId = transactionId;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    protected SavingsTransactionDate(Parcel in) {
        this.transactionId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.year = (Integer) in.readValue(Integer.class.getClassLoader());
        this.month = (Integer) in.readValue(Integer.class.getClassLoader());
        this.day = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<SavingsTransactionDate> CREATOR =
            new Parcelable.Creator<SavingsTransactionDate>() {
        @Override
        public SavingsTransactionDate createFromParcel(Parcel source) {
            return new SavingsTransactionDate(source);
        }

        @Override
        public SavingsTransactionDate[] newArray(int size) {
            return new SavingsTransactionDate[size];
        }
    };
}

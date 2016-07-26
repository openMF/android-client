package com.mifos.objects.accounts.loan;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * This Model is for saving the ActualDisbursementDate of LoanWithAssociations's Timeline
 * This Model is only for Database use.
 * Created by Rajan Maurya on 26/07/16.
 */
@Table(database = MifosDatabase.class )
@ModelContainer
public class ActualDisbursementDate extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    Integer loanId;

    @Column
    Integer year;

    @Column
    Integer month;

    @Column
    Integer date;

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
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

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }


    public ActualDisbursementDate(Integer loanId, Integer year, Integer month, Integer date) {
        this.date = date;
        this.loanId = loanId;
        this.year = year;
        this.month = month;
    }

    public ActualDisbursementDate() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.loanId);
        dest.writeValue(this.year);
        dest.writeValue(this.month);
        dest.writeValue(this.date);
    }

    protected ActualDisbursementDate(Parcel in) {
        this.loanId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.year = (Integer) in.readValue(Integer.class.getClassLoader());
        this.month = (Integer) in.readValue(Integer.class.getClassLoader());
        this.date = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<ActualDisbursementDate> CREATOR = new
            Creator<ActualDisbursementDate>() {
        @Override
        public ActualDisbursementDate createFromParcel(Parcel source) {
            return new ActualDisbursementDate(source);
        }

        @Override
        public ActualDisbursementDate[] newArray(int size) {
            return new ActualDisbursementDate[size];
        }
    };
}

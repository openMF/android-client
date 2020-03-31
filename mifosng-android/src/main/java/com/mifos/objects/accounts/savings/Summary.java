/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.savings;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

@Table(database = MifosDatabase.class, name = "SavingsAccountSummary")
@ModelContainer
public class Summary extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    transient Integer savingsId;

    @SerializedName("currency")
    Currency currency;

    @SerializedName("totalDeposits")
    @Column
    Double totalDeposits;

    @SerializedName("accountBalance")
    @Column
    Double accountBalance;

    @SerializedName("totalWithdrawals")
    @Column
    Double totalWithdrawals;

    @SerializedName("totalInterestEarned")
    @Column
    Double totalInterestEarned;

    public Integer getSavingsId() {
        return savingsId;
    }

    public void setSavingsId(Integer savingsId) {
        this.savingsId = savingsId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getTotalDeposits() {
        return totalDeposits;
    }

    public void setTotalDeposits(Double totalDeposits) {
        this.totalDeposits = totalDeposits;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Double getTotalWithdrawals() {
        return totalWithdrawals;
    }

    public void setTotalWithdrawals(Double totalWithdrawals) {
        this.totalWithdrawals = totalWithdrawals;
    }

    public Double getTotalInterestEarned() {
        return totalInterestEarned;
    }

    public void setTotalInterestEarned(Double totalInterestEarned) {
        this.totalInterestEarned = totalInterestEarned;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.currency, flags);
        dest.writeValue(this.totalDeposits);
        dest.writeValue(this.accountBalance);
        dest.writeValue(this.totalWithdrawals);
        dest.writeValue(this.totalInterestEarned);
    }

    public Summary() {
    }

    protected Summary(Parcel in) {
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        this.totalDeposits = (Double) in.readValue(Double.class.getClassLoader());
        this.accountBalance = (Double) in.readValue(Double.class.getClassLoader());
        this.totalWithdrawals = (Double) in.readValue(Double.class.getClassLoader());
        this.totalInterestEarned = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<Summary> CREATOR = new Parcelable.Creator<Summary>() {
        @Override
        public Summary createFromParcel(Parcel source) {
            return new Summary(source);
        }

        @Override
        public Summary[] newArray(int size) {
            return new Summary[size];
        }
    };
}

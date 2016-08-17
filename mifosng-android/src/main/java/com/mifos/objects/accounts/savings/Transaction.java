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
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

@Table(database = MifosDatabase.class)
@ModelContainer
public class Transaction extends MifosBaseModel implements Parcelable {

    @SerializedName("id")
    @PrimaryKey
    Integer id;

    @SerializedName("savingsAccountId")
    @Column
    transient Integer savingsAccountId;

    @SerializedName("transactionType")
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    TransactionType transactionType;

    @SerializedName("accountId")
    Integer accountId;

    @SerializedName("accountNo")
    String accountNo;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    transient SavingsTransactionDate savingsTransactionDate;

    @SerializedName("date")
    List<Integer> date = new ArrayList<>();

    @SerializedName("currency")
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Currency currency;

    @SerializedName("amount")
    @Column
    Double amount;

    @SerializedName("runningBalance")
    @Column
    Double runningBalance;

    @SerializedName("reversed")
    Boolean reversed;

    public Integer getSavingsAccountId() {
        return savingsAccountId;
    }

    public void setSavingsAccountId(Integer savingsAccountId) {
        this.savingsAccountId = savingsAccountId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public List<Integer> getDate() {
        return date;
    }

    public void setDate(List<Integer> date) {
        this.date = date;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getRunningBalance() {
        return runningBalance;
    }

    public void setRunningBalance(Double runningBalance) {
        this.runningBalance = runningBalance;
    }

    public Boolean getReversed() {
        return reversed;
    }

    public void setReversed(Boolean reversed) {
        this.reversed = reversed;
    }

    public SavingsTransactionDate getSavingsTransactionDate() {
        return savingsTransactionDate;
    }

    public void setSavingsTransactionDate(SavingsTransactionDate savingsTransactionDate) {
        this.savingsTransactionDate = savingsTransactionDate;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", savingsAccountId=" + savingsAccountId +
                ", transactionType=" + transactionType +
                ", accountId=" + accountId +
                ", accountNo='" + accountNo + '\'' +
                ", savingsTransactionDate=" + savingsTransactionDate +
                ", date=" + date +
                ", currency=" + currency +
                ", amount=" + amount +
                ", runningBalance=" + runningBalance +
                ", reversed=" + reversed +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeParcelable(this.transactionType, flags);
        dest.writeValue(this.accountId);
        dest.writeString(this.accountNo);
        dest.writeList(this.date);
        dest.writeParcelable(this.currency, flags);
        dest.writeValue(this.amount);
        dest.writeValue(this.runningBalance);
        dest.writeValue(this.reversed);
    }

    public Transaction() {
    }

    protected Transaction(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.transactionType = in.readParcelable(TransactionType.class.getClassLoader());
        this.accountId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.accountNo = in.readString();
        this.date = new ArrayList<Integer>();
        in.readList(this.date, Integer.class.getClassLoader());
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.runningBalance = (Double) in.readValue(Double.class.getClassLoader());
        this.reversed = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Transaction> CREATOR =
            new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}

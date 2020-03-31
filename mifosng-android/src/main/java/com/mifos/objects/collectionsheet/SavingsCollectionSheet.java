package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.objects.accounts.savings.Currency;

/**
 * Created by Tarun on 31-07-17.
 */

public class SavingsCollectionSheet implements Parcelable {

    //The accountId is of String type only. It's not a mistake.
    private String accountId;

    private int accountStatusId;

    private Currency currency;

    private String depositAccountType;

    private int dueAmount;

    private int productId;

    private String productName;

    private int savingsId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getAccountStatusId() {
        return accountStatusId;
    }

    public void setAccountStatusId(int accountStatusId) {
        this.accountStatusId = accountStatusId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getDepositAccountType() {
        return depositAccountType;
    }

    public void setDepositAccountType(String depositAccountType) {
        this.depositAccountType = depositAccountType;
    }

    public int getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(int dueAmount) {
        this.dueAmount = dueAmount;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getSavingsId() {
        return savingsId;
    }

    public void setSavingsId(int savingsId) {
        this.savingsId = savingsId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.accountId);
        dest.writeInt(this.accountStatusId);
        dest.writeParcelable(this.currency, flags);
        dest.writeString(this.depositAccountType);
        dest.writeInt(this.dueAmount);
        dest.writeInt(this.productId);
        dest.writeString(this.productName);
        dest.writeInt(this.savingsId);
    }

    public SavingsCollectionSheet() {
    }

    protected SavingsCollectionSheet(Parcel in) {
        this.accountId = in.readString();
        this.accountStatusId = in.readInt();
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        this.depositAccountType = in.readString();
        this.dueAmount = in.readInt();
        this.productId = in.readInt();
        this.productName = in.readString();
        this.savingsId = in.readInt();
    }

    public static final Parcelable.Creator<SavingsCollectionSheet> CREATOR = new
            Parcelable.Creator<SavingsCollectionSheet>() {
        @Override
        public SavingsCollectionSheet createFromParcel(Parcel source) {
            return new SavingsCollectionSheet(source);
        }

        @Override
        public SavingsCollectionSheet[] newArray(int size) {
            return new SavingsCollectionSheet[size];
        }
    };
}

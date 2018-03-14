package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tarun on 31-07-17.
 */

public class BulkSavingsDueTransaction implements Parcelable {

    private int savingsId;

    private String transactionAmount;

    public int getSavingsId() {
        return savingsId;
    }

    public void setSavingsId(int savingsId) {
        this.savingsId = savingsId;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BulkSavingsDueTransaction(int savingsId, String transactionAmount) {
        this.savingsId = savingsId;
        this.transactionAmount = transactionAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.savingsId);
        dest.writeString(this.transactionAmount);
    }

    protected BulkSavingsDueTransaction(Parcel in) {
        this.savingsId = in.readInt();
        this.transactionAmount = in.readString();
    }

    public static final Creator<BulkSavingsDueTransaction> CREATOR = new
            Creator<BulkSavingsDueTransaction>() {
        @Override
        public BulkSavingsDueTransaction createFromParcel(Parcel source) {
            return new BulkSavingsDueTransaction(source);
        }

        @Override
        public BulkSavingsDueTransaction[] newArray(int size) {
            return new BulkSavingsDueTransaction[size];
        }
    };
}

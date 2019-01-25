package com.mifos.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tarun on 11-07-2017.
 */

public class IndividualCollectionSheetPayload implements Parcelable {

    public List<BulkRepaymentTransactions> bulkRepaymentTransactions = new ArrayList<>();

    private String actualDisbursementDate;

    //TODO Change to BulkDisbursementTransaction list later
    private List<BulkRepaymentTransactions> bulkDisbursementTransactions = new ArrayList<>();

    //TODO Change to BulkSavingsDueTransaction list later
    private List<BulkRepaymentTransactions> bulkSavingsDueTransactions = new ArrayList<>();

    private String dateFormat = "dd MMMM YYYY";

    private String locale = "en";

    private String transactionDate;

    public List<BulkRepaymentTransactions> getBulkRepaymentTransactions() {
        return bulkRepaymentTransactions;
    }

    public void setBulkRepaymentTransactions(
            List<BulkRepaymentTransactions> bulkRepaymentTransactions) {
        this.bulkRepaymentTransactions = bulkRepaymentTransactions;
    }

    public String getActualDisbursementDate() {
        return actualDisbursementDate;
    }

    public void setActualDisbursementDate(String actualDisbursementDate) {
        this.actualDisbursementDate = actualDisbursementDate;
    }

    public List<BulkRepaymentTransactions> getBulkDisbursementTransactions() {
        return bulkDisbursementTransactions;
    }

    public void setBulkDisbursementTransactions(
            List<BulkRepaymentTransactions> bulkDisbursementTransactions) {
        this.bulkDisbursementTransactions = bulkDisbursementTransactions;
    }

    public List<BulkRepaymentTransactions> getBulkSavingsDueTransactions() {
        return bulkSavingsDueTransactions;
    }

    public void setBulkSavingsDueTransactions(
            List<BulkRepaymentTransactions> bulkSavingsDueTransactions) {
        this.bulkSavingsDueTransactions = bulkSavingsDueTransactions;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.bulkRepaymentTransactions);
        dest.writeString(this.actualDisbursementDate);
        dest.writeTypedList(this.bulkDisbursementTransactions);
        dest.writeTypedList(this.bulkSavingsDueTransactions);
        dest.writeString(this.dateFormat);
        dest.writeString(this.locale);
        dest.writeString(this.transactionDate);
    }

    public IndividualCollectionSheetPayload() {
    }

    protected IndividualCollectionSheetPayload(Parcel in) {
        this.bulkRepaymentTransactions = in.createTypedArrayList(BulkRepaymentTransactions.CREATOR);
        this.actualDisbursementDate = in.readString();
        this.bulkDisbursementTransactions =
                in.createTypedArrayList(BulkRepaymentTransactions.CREATOR);
        this.bulkSavingsDueTransactions =
                in.createTypedArrayList(BulkRepaymentTransactions.CREATOR);
        this.dateFormat = in.readString();
        this.locale = in.readString();
        this.transactionDate = in.readString();
    }

    public static final Parcelable.Creator<IndividualCollectionSheetPayload> CREATOR = new
            Parcelable.Creator<IndividualCollectionSheetPayload>() {
        @Override
        public IndividualCollectionSheetPayload createFromParcel(Parcel source) {
            return new IndividualCollectionSheetPayload(source);
        }

        @Override
        public IndividualCollectionSheetPayload[] newArray(int size) {
            return new IndividualCollectionSheetPayload[size];
        }
    };

    @Override
    public String toString() {
        return bulkRepaymentTransactions.toString();
    }
}

package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.model.BulkRepaymentTransactions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tarun on 25-07-2017.
 */

public class ProductiveCollectionSheetPayload implements Parcelable {

    private List<BulkRepaymentTransactions> bulkRepaymentTransactions = new ArrayList<>();

    private int calendarId;

    private String dateFormat = "dd MMMM yyyy";

    private String locale = "en";

    private String transactionDate;

    public List<BulkRepaymentTransactions> getBulkRepaymentTransactions() {
        return bulkRepaymentTransactions;
    }

    public void setBulkRepaymentTransactions(List<BulkRepaymentTransactions>
                                                     bulkRepaymentTransactions) {
        this.bulkRepaymentTransactions = bulkRepaymentTransactions;
    }

    public int getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(int calendarId) {
        this.calendarId = calendarId;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
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
        dest.writeInt(this.calendarId);
        dest.writeString(this.dateFormat);
        dest.writeString(this.locale);
        dest.writeString(this.transactionDate);
    }

    public ProductiveCollectionSheetPayload() {
    }

    protected ProductiveCollectionSheetPayload(Parcel in) {
        this.bulkRepaymentTransactions = in.createTypedArrayList(BulkRepaymentTransactions.CREATOR);
        this.calendarId = in.readInt();
        this.dateFormat = in.readString();
        this.locale = in.readString();
        this.transactionDate = in.readString();
    }

    public static final Parcelable.Creator<ProductiveCollectionSheetPayload> CREATOR = new
            Parcelable.Creator<ProductiveCollectionSheetPayload>() {
        @Override
        public ProductiveCollectionSheetPayload createFromParcel(Parcel source) {
            return new ProductiveCollectionSheetPayload(source);
        }

        @Override
        public ProductiveCollectionSheetPayload[] newArray(int size) {
            return new ProductiveCollectionSheetPayload[size];
        }
    };
}

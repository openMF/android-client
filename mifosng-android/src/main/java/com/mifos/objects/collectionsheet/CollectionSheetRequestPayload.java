package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tarun on 25-07-2017.
 */

public class CollectionSheetRequestPayload implements Parcelable {

    private int calendarId;

    private String dateFormat = "dd MMM yyyy";

    private String locale = "en";

    private String transactionDate;

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
        dest.writeInt(this.calendarId);
        dest.writeString(this.dateFormat);
        dest.writeString(this.locale);
        dest.writeString(this.transactionDate);
    }

    public CollectionSheetRequestPayload() {
    }

    protected CollectionSheetRequestPayload(Parcel in) {
        this.calendarId = in.readInt();
        this.dateFormat = in.readString();
        this.locale = in.readString();
        this.transactionDate = in.readString();
    }

    public static final Parcelable.Creator<CollectionSheetRequestPayload> CREATOR = new
            Parcelable.Creator<CollectionSheetRequestPayload>() {
        @Override
        public CollectionSheetRequestPayload createFromParcel(Parcel source) {
            return new CollectionSheetRequestPayload(source);
        }

        @Override
        public CollectionSheetRequestPayload[] newArray(int size) {
            return new CollectionSheetRequestPayload[size];
        }
    };
}

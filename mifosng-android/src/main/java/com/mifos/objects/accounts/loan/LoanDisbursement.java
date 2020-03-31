/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.objects.accounts.loan;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class LoanDisbursement implements Parcelable {

    @SerializedName("actualDisbursementDate")
    String actualDisbursementDate;

    @SerializedName("note")
    String note;

    @SerializedName("transactionAmount")
    Double transactionAmount;

    @SerializedName("paymentTypeId")
    Integer paymentTypeId;

    String locale = "en";
    String dateFormat = "dd MMMM yyyy";

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getActualDisbursementDate() {
        return actualDisbursementDate;
    }

    public void setActualDisbursementDate(String actualDisbursementDate) {
        this.actualDisbursementDate = actualDisbursementDate;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Integer getPaymentId() {
        return paymentTypeId;
    }

    public void setPaymentId(Integer paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.actualDisbursementDate);
        dest.writeString(this.note);
        dest.writeValue(this.transactionAmount);
        dest.writeValue(this.paymentTypeId);
        dest.writeString(this.locale);
        dest.writeString(this.dateFormat);
    }

    public LoanDisbursement() {
    }

    protected LoanDisbursement(Parcel in) {
        this.actualDisbursementDate = in.readString();
        this.note = in.readString();
        this.transactionAmount = (Double) in.readValue(Double.class.getClassLoader());
        this.paymentTypeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.locale = in.readString();
        this.dateFormat = in.readString();
    }

    public static final Parcelable.Creator<LoanDisbursement> CREATOR =
            new Parcelable.Creator<LoanDisbursement>() {
        @Override
        public LoanDisbursement createFromParcel(Parcel source) {
            return new LoanDisbursement(source);
        }

        @Override
        public LoanDisbursement[] newArray(int size) {
            return new LoanDisbursement[size];
        }
    };
}

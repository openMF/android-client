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

/**
 * Created by ishankhanna on 12/06/14.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class SavingsAccountTransactionRequest extends MifosBaseModel implements Parcelable {

    @SerializedName("savingAccountId")
    @PrimaryKey
    transient Integer savingAccountId;

    @SerializedName("dateFormat")
    @Column
    String dateFormat;

    @SerializedName("locale")
    @Column
    String locale;

    @SerializedName("transactionDate")
    @Column
    String transactionDate;

    @SerializedName("transactionAmount")
    @Column
    String transactionAmount;

    @SerializedName("paymentTypeId")
    @Column
    String paymentTypeId;

    @SerializedName("note")
    @Column
    String note;

    @SerializedName("accountNumber")
    @Column
    String accountNumber;

    @SerializedName("checkNumber")
    @Column
    String checkNumber;

    @SerializedName("routingCode")
    @Column
    String routingCode;

    @SerializedName("receiptNumber")
    @Column
    String receiptNumber;

    @SerializedName("bankNumber")
    @Column
    String bankNumber;

    public Integer getSavingAccountId() {
        return savingAccountId;
    }

    public void setSavingAccountId(Integer savingAccountId) {
        this.savingAccountId = savingAccountId;
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

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(String paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getRoutingCode() {
        return routingCode;
    }

    public void setRoutingCode(String routingCode) {
        this.routingCode = routingCode;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dateFormat);
        dest.writeString(this.locale);
        dest.writeString(this.transactionDate);
        dest.writeString(this.transactionAmount);
        dest.writeString(this.paymentTypeId);
        dest.writeString(this.note);
        dest.writeString(this.accountNumber);
        dest.writeString(this.checkNumber);
        dest.writeString(this.routingCode);
        dest.writeString(this.receiptNumber);
        dest.writeString(this.bankNumber);
    }

    public SavingsAccountTransactionRequest() {
    }

    protected SavingsAccountTransactionRequest(Parcel in) {
        this.dateFormat = in.readString();
        this.locale = in.readString();
        this.transactionDate = in.readString();
        this.transactionAmount = in.readString();
        this.paymentTypeId = in.readString();
        this.note = in.readString();
        this.accountNumber = in.readString();
        this.checkNumber = in.readString();
        this.routingCode = in.readString();
        this.receiptNumber = in.readString();
        this.bankNumber = in.readString();
    }

    public static final Parcelable.Creator<SavingsAccountTransactionRequest> CREATOR =
            new Parcelable.Creator<SavingsAccountTransactionRequest>() {
        @Override
        public SavingsAccountTransactionRequest createFromParcel(Parcel source) {
            return new SavingsAccountTransactionRequest(source);
        }

        @Override
        public SavingsAccountTransactionRequest[] newArray(int size) {
            return new SavingsAccountTransactionRequest[size];
        }
    };
}

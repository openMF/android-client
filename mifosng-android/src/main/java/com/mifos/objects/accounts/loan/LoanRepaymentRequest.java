/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

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
 * Created by ishankhanna on 22/05/14.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class LoanRepaymentRequest extends MifosBaseModel implements Parcelable {


    @PrimaryKey
    transient long timeStamp;

    @Column
    transient Integer loanId;

    @Column
    transient String errorMessage;

    @Column
    String dateFormat;

    @Column
    String locale;

    @Column
    String transactionDate;

    @Column
    String transactionAmount;

    @Column
    String paymentTypeId;

    @Column
    String note;

    @Column
    String accountNumber;

    @Column
    String checkNumber;

    @Column
    String routingCode;

    @Column
    String receiptNumber;

    @Column
    String bankNumber;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
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

    public LoanRepaymentRequest() {
    }

    protected LoanRepaymentRequest(Parcel in) {
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

    public static final Parcelable.Creator<LoanRepaymentRequest> CREATOR =
            new Parcelable.Creator<LoanRepaymentRequest>() {
        @Override
        public LoanRepaymentRequest createFromParcel(Parcel source) {
            return new LoanRepaymentRequest(source);
        }

        @Override
        public LoanRepaymentRequest[] newArray(int size) {
            return new LoanRepaymentRequest[size];
        }
    };
}

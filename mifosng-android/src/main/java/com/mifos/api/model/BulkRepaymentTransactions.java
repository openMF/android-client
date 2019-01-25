/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class BulkRepaymentTransactions implements Parcelable {
    public int loanId;
    public double transactionAmount;

    //Optional fields

    private String accountNumber;
    private String bankNumber;
    private String checkNumber;
    private Integer paymentTypeId;
    private String receiptNumber;
    private String routingCode;

    public BulkRepaymentTransactions() {
    }

    public BulkRepaymentTransactions(int loanId, double transactionAmount) {
        this.loanId = loanId;
        this.transactionAmount = transactionAmount;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public Integer getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(Integer paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getRoutingCode() {
        return routingCode;
    }

    public void setRoutingCode(String routingCode) {
        this.routingCode = routingCode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.loanId);
        dest.writeDouble(this.transactionAmount);
        dest.writeString(this.accountNumber);
        dest.writeString(this.bankNumber);
        dest.writeString(this.checkNumber);
        dest.writeValue(this.paymentTypeId);
        dest.writeString(this.receiptNumber);
        dest.writeString(this.routingCode);
    }

    protected BulkRepaymentTransactions(Parcel in) {
        this.loanId = in.readInt();
        this.transactionAmount = in.readDouble();
        this.accountNumber = in.readString();
        this.bankNumber = in.readString();
        this.checkNumber = in.readString();
        this.paymentTypeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.receiptNumber = in.readString();
        this.routingCode = in.readString();
    }

    public static final Parcelable.Creator<BulkRepaymentTransactions> CREATOR =
            new Parcelable.Creator<BulkRepaymentTransactions>() {
        @Override
        public BulkRepaymentTransactions createFromParcel(Parcel source) {
            return new BulkRepaymentTransactions(source);
        }

        @Override
        public BulkRepaymentTransactions[] newArray(int size) {
            return new BulkRepaymentTransactions[size];
        }
    };
}

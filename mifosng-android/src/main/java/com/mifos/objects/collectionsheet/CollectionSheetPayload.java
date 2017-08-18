package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.model.BulkRepaymentTransactions;
import com.mifos.api.model.ClientsAttendance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tarun on 31-07-17.
 */

public class CollectionSheetPayload implements Parcelable {

    private String actualDisbursementDate;

    private List<BulkRepaymentTransactions> bulkRepaymentTransactions = new ArrayList<>();

    private List<BulkSavingsDueTransaction> bulkSavingsDueTransactions = new ArrayList<>();

    private int calendarId;

    private List<ClientsAttendance> clientsAttendance = new ArrayList<>();

    private String dateFormat = "dd MMMM yyyy";

    private String locale = "en";

    private String transactionDate;

    private String accountNumber;

    private String bankNumber;

    private String checkNumber;

    private Integer paymentTypeId;

    private String receiptNumber;

    private String routingCode;

    public String getActualDisbursementDate() {
        return actualDisbursementDate;
    }

    public void setActualDisbursementDate(String actualDisbursementDate) {
        this.actualDisbursementDate = actualDisbursementDate;
    }

    public List<BulkRepaymentTransactions> getBulkRepaymentTransactions() {
        return bulkRepaymentTransactions;
    }

    public void setBulkRepaymentTransactions(List<BulkRepaymentTransactions>
                                                     bulkRepaymentTransactions) {
        this.bulkRepaymentTransactions = bulkRepaymentTransactions;
    }

    public List<BulkSavingsDueTransaction> getBulkSavingsDueTransactions() {
        return bulkSavingsDueTransactions;
    }

    public void setBulkSavingsDueTransactions(List<BulkSavingsDueTransaction>
                                                      bulkSavingsDueTransactions) {
        this.bulkSavingsDueTransactions = bulkSavingsDueTransactions;
    }

    public int getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(int calendarId) {
        this.calendarId = calendarId;
    }

    public List<ClientsAttendance> getClientsAttendance() {
        return clientsAttendance;
    }

    public void setClientsAttendance(List<ClientsAttendance> clientsAttendance) {
        this.clientsAttendance = clientsAttendance;
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

    public int getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(int paymentTypeId) {
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
        dest.writeString(this.actualDisbursementDate);
        dest.writeTypedList(this.bulkRepaymentTransactions);
        dest.writeTypedList(this.bulkSavingsDueTransactions);
        dest.writeInt(this.calendarId);
        dest.writeTypedList(this.clientsAttendance);
        dest.writeString(this.dateFormat);
        dest.writeString(this.locale);
        dest.writeString(this.transactionDate);
        dest.writeString(this.accountNumber);
        dest.writeString(this.bankNumber);
        dest.writeString(this.checkNumber);
        dest.writeInt(this.paymentTypeId);
        dest.writeString(this.receiptNumber);
        dest.writeString(this.routingCode);
    }

    public CollectionSheetPayload() {
    }

    protected CollectionSheetPayload(Parcel in) {
        this.actualDisbursementDate = in.readString();
        this.bulkRepaymentTransactions = in.createTypedArrayList(BulkRepaymentTransactions.CREATOR);
        this.bulkSavingsDueTransactions = in
                .createTypedArrayList(BulkSavingsDueTransaction.CREATOR);
        this.calendarId = in.readInt();
        this.clientsAttendance = in.createTypedArrayList(ClientsAttendance.CREATOR);
        this.dateFormat = in.readString();
        this.locale = in.readString();
        this.transactionDate = in.readString();
        this.accountNumber = in.readString();
        this.bankNumber = in.readString();
        this.checkNumber = in.readString();
        this.paymentTypeId = in.readInt();
        this.receiptNumber = in.readString();
        this.routingCode = in.readString();
    }

    public static final Creator<CollectionSheetPayload> CREATOR = new
            Creator<CollectionSheetPayload>() {
        @Override
        public CollectionSheetPayload createFromParcel(Parcel source) {
            return new CollectionSheetPayload(source);
        }

        @Override
        public CollectionSheetPayload[] newArray(int size) {
            return new CollectionSheetPayload[size];
        }
    };
}

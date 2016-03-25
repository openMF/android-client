/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.objects.accounts.loan;

public class LoanDisbursement {

    private String locale = "en";
    private String dateFormat = "dd MMMM yyyy";
    private String actualDisbursementDate;
    private String note;
    private String transactionAmount;
    private int paymentTypeId;

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

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public int getPaymentId() {
        return paymentTypeId;
    }

    public void setPaymentId(int paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }
}

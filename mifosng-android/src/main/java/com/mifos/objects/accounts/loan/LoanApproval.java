/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.objects.accounts.loan;

import com.google.gson.annotations.SerializedName;

public class LoanApproval {

    @SerializedName("approvedOnDate")
    String approvedOnDate;

    @SerializedName("approvedLoanAmount")
    String approvedLoanAmount;

    @SerializedName("expectedDisbursementDate")
    String expectedDisbursementDate;

    @SerializedName("note")
    String note;

    String locale = "en";
    String dateFormat = "dd MMMM yyyy";

    public String getExpectedDisbursementDate() {
        return expectedDisbursementDate;
    }

    public void setExpectedDisbursementDate(String expectedDisbursementDate) {
        this.expectedDisbursementDate = expectedDisbursementDate;
    }

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

    public String getApprovedOnDate() {
        return approvedOnDate;
    }

    public void setApprovedOnDate(String approvedOnDate) {
        this.approvedOnDate = approvedOnDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getApprovedLoanAmount() {
        return approvedLoanAmount;
    }

    public void setApprovedLoanAmount(String approvedLoanAmount) {
        this.approvedLoanAmount = approvedLoanAmount;
    }
}

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.loan;

/**
 * Created by ishankhanna on 23/06/14.
 */
public class LoanApprovalRequest {

    private String locale = "en";
    private String dateFormat = "dd MM yyyy";
    private String approvedOnDate;
    private String note;

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
}

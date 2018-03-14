package com.mifos.api.model;

/**
 * Created by Tarun on 06-07-2017.
 */

public class RequestCollectionSheetPayload {

    private String dateFormat = "dd MMMM yyyy";

    private String locale = "en";

    private int officeId;

    private int staffId;

    private String transactionDate;

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }
}

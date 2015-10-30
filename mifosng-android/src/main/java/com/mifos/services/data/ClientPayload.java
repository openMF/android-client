package com.mifos.services.data;

/**
 * Created by ADMIN on 16-Jun-15.
 */
public class ClientPayload extends DefaultPayload {

    private String firstname;
    private String lastname;
    private String middlename;
    private int officeId;
    private boolean active;
    private String activationDate;
    private String submittedOnDate;
    private String dateOfBirth;
    private String mobileNo;
    private String externalId;


    public String getFirstname() {
        return firstname;
    }

    public void setMiddlename(String mobilenumber) {
        this.middlename = middlename;
    }
    public String getMiddlename() {
        return middlename;
    }
    public String getMobileno() {
        return mobileNo;
    }
    public void setMobileno(String mobileNo) {
        this.mobileNo = mobileNo;
    }
    public String getExternalId() {
        return externalId;
    }
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }
    public String getDateofBirth() {
        return dateOfBirth;
    }

    public void setDateofBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getSubmittedOnDate() {
        return submittedOnDate;
    }

    public void setSubmittedOnDate(String submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }
}

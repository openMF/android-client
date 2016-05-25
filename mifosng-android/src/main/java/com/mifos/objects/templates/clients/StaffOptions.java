package com.mifos.objects.templates.clients;

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

import com.google.gson.annotations.SerializedName;


/**
 * Created by rajan on 13/3/16.
 */
public class StaffOptions {

    private int id;
    private String firstname;
    private String lastname;
    private String displayName;
    private int officeId;
    private String officeName;

    @SerializedName("isLoanOfficer")
    private boolean isLoan_officer;

    @SerializedName("isActive")
    private boolean is_Active;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public boolean isLoanOfficer() {
        return isLoan_officer;
    }

    public void setIsLoanOfficer(boolean isLoanOfficer) {
        this.isLoan_officer = isLoanOfficer;
    }

    public boolean isActive() {
        return is_Active;
    }

    public void setIsActive(boolean isActive) {
        this.is_Active = isActive;
    }

    @Override
    public String toString() {
        return "StaffOptions{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", displayName='" + displayName + '\'' +
                ", officeId=" + officeId +
                ", officeName='" + officeName + '\'' +
                ", isLoanOfficer=" + isLoan_officer +
                ", isActive=" + is_Active +
                '}';
    }
}

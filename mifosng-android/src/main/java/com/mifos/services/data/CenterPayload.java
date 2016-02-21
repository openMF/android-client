/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.services.data;

/**
 * Created by nellyk on 1/22/2016.
 */
public class CenterPayload {

    private String centername;
    private int officeId;
    private int staffId;
    private boolean active;
    private String activationDate;
    private String submittedOnDate;
    private String externalId;

    public String getCenterName() {
        return centername;
    }

    public void setCenterName(String centername) {
        this.centername = centername;
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

    public String setSubmissionDate() {
        return submittedOnDate;
    }

    public void setSubmissionDate(String submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}

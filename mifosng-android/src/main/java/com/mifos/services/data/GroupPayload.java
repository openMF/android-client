/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.services.data;

import java.util.List;

/**
 * Created by nellyk on 1/22/2016.
 */
public class GroupPayload {

    private String groupname;
    private int officeId;
    private boolean active;
    private String activationDate;
    private String submittedOnDate;
    private String externalId;
    private int staffId;
    private String name;
    private List<Integer> clientMembers;
    private String dateFormat = "dd MMMM yyyy";
    private String locale;

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getGroupName() {
        return groupname;
    }

    public void setGroupName(String centername) {
        this.groupname = centername;
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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}

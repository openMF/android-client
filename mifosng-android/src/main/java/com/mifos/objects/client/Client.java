/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.client;

import com.mifos.objects.Status;
import com.mifos.objects.Timeline;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ishankhanna on 08/02/14.
 */
public class Client {

    private int id;
    private String accountNo;
    private String mobileNo;
    private Status status;
    private boolean active;
    private List<Integer> activationDate = new ArrayList<Integer>();
    private String firstname;
    private String middlename;
    private String lastname;
    private String displayName;
    private int officeId;
    private String officeName;
    private int staffId;
    private String staffName;
    private Timeline timeline;
    private String fullname;
    private int imageId;
    private boolean imagePresent;
    private String externalId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
     public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Integer> getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(List<Integer> activationDate) {
        this.activationDate = activationDate;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public boolean isImagePresent() {
        return imagePresent;
    }

    public void setImagePresent(boolean imagePresent) {
        this.imagePresent = imagePresent;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getFormattedActivationDateAsString()
    {
        //TODO Format Date Based on Current Locale
        /**
         * Integer List Contains
         * YYYY
         * MM
         * DD
         */

        Calendar calendar = Calendar.getInstance();
        // Note, java.util.Calendar month is zero-indexed.
        calendar.set(activationDate.get(0),activationDate.get(1)-1,activationDate.get(2));

        DateFormat dateFormat = DateFormat.getDateInstance();
        return dateFormat.format(calendar.getTime()).toString();

    }
    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", accountNo='" + accountNo + '\'' +
                ", status=" + status +
                ", active=" + active +
                ", activationDate=" + activationDate +
                ", firstname='" + firstname + '\'' +
                ", middlename='" + middlename + '\'' +
                ", lastname='" + lastname + '\'' +
                ", displayName='" + displayName + '\'' +
                ", officeId=" + officeId +
                ", officeName='" + officeName + '\'' +
                ", staffId=" + staffId +
                ", staffName='" + staffName + '\'' +
                ", timeline=" + timeline +
                ", fullname='" + fullname + '\'' +
                ", imageId=" + imageId +
                ", imagePresent=" + imagePresent +
                ", externalId='" + externalId + '\'' +
                '}';
    }
}

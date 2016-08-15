/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.db;

import com.google.gson.Gson;

import java.util.List;


public class MeetingCenter {

    private String name;
    private String externalId;
    private int officeId;
    private int staffId;
    private String staffName;
    private Status status;
    private boolean active;
    private MeetingDate meetingDate;
    private CollectionMeetingCalendar collectionMeetingCalendar;
    private int isSynced;
    private long centerId;

    private List<Integer> activationDate;

    public long getCenterId() {
        return centerId;
    }

    public void setCenterId(long centerId) {
        this.centerId = centerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Integer> getActivationDate() {
        return activationDate;
    }


    public void setActivationDate(List<Integer> activationDate) {
        this.activationDate = activationDate;
    }

    public int getIsSynced() {
        return isSynced;
    }

    public void setIsSynced(int isSynced) {
        this.isSynced = isSynced;
    }

    public CollectionMeetingCalendar getCollectionMeetingCalendar() {
        return collectionMeetingCalendar;
    }

    public void setCollectionMeetingCalendar(CollectionMeetingCalendar collectionMeetingCalendar) {
        this.collectionMeetingCalendar = collectionMeetingCalendar;
    }

    public MeetingDate getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(MeetingDate meetingDate) {
        this.meetingDate = meetingDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}

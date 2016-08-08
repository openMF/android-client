/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.db;


import com.google.gson.Gson;

public class OfflineCenter {
    private int staffId;
    private String staffName;

    private MeetingCenter[] meetingFallCenters;

    public MeetingCenter[] getMeetingFallCenters() {
        return meetingFallCenters;
    }

    public void setMeetingFallCenters(MeetingCenter[] meetingFallCenters) {
        this.meetingFallCenters = meetingFallCenters;
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

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}

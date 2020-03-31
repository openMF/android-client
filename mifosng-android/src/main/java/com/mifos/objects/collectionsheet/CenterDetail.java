package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tarun on 25-07-2017.
 */

public class CenterDetail implements Parcelable {

    private int staffId;

    private String staffName;

    private List<MeetingFallCalendar> meetingFallCenters = new ArrayList<>();

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

    public List<MeetingFallCalendar> getMeetingFallCenters() {
        return meetingFallCenters;
    }

    public void setMeetingFallCenters(List<MeetingFallCalendar> meetingFallCenters) {
        this.meetingFallCenters = meetingFallCenters;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.staffId);
        dest.writeString(this.staffName);
        dest.writeTypedList(this.meetingFallCenters);
    }

    public CenterDetail() {
    }

    protected CenterDetail(Parcel in) {
        this.staffId = in.readInt();
        this.staffName = in.readString();
        this.meetingFallCenters = in.createTypedArrayList(MeetingFallCalendar.CREATOR);
    }

    public static final Parcelable.Creator<CenterDetail> CREATOR = new
            Parcelable.Creator<CenterDetail>() {
        @Override
        public CenterDetail createFromParcel(Parcel source) {
            return new CenterDetail(source);
        }

        @Override
        public CenterDetail[] newArray(int size) {
            return new CenterDetail[size];
        }
    };
}

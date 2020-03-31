package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.objects.accounts.loan.Status;

/**
 * Created by Tarun on 25-07-2017.
 */

public class MeetingFallCalendar implements Parcelable {

    //It's not a mistake. This AccountNo field DOES expect a String.
    private String accountNo;

    private int[] activationDate;

    private boolean active;

    private CollectionMeetingCalendar collectionMeetingCalendar;

    private String hierarchy;

    private int id;

    private int installmentDue;

    private String name;

    private int officeId;

    private int staffId;

    private String staffName;

    private Status status;

    private int totalCollected;

    private int totalOverdue;

    private int totaldue;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public int[] getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(int[] activationDate) {
        this.activationDate = activationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public CollectionMeetingCalendar getCollectionMeetingCalendar() {
        return collectionMeetingCalendar;
    }

    public void setCollectionMeetingCalendar(CollectionMeetingCalendar collectionMeetingCalendar) {
        this.collectionMeetingCalendar = collectionMeetingCalendar;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInstallmentDue() {
        return installmentDue;
    }

    public void setInstallmentDue(int installmentDue) {
        this.installmentDue = installmentDue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getTotalCollected() {
        return totalCollected;
    }

    public void setTotalCollected(int totalCollected) {
        this.totalCollected = totalCollected;
    }

    public int getTotalOverdue() {
        return totalOverdue;
    }

    public void setTotalOverdue(int totalOverdue) {
        this.totalOverdue = totalOverdue;
    }

    public int getTotaldue() {
        return totaldue;
    }

    public void setTotaldue(int totaldue) {
        this.totaldue = totaldue;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.accountNo);
        dest.writeIntArray(this.activationDate);
        dest.writeByte(this.active ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.collectionMeetingCalendar, flags);
        dest.writeString(this.hierarchy);
        dest.writeInt(this.id);
        dest.writeInt(this.installmentDue);
        dest.writeString(this.name);
        dest.writeInt(this.officeId);
        dest.writeInt(this.staffId);
        dest.writeString(this.staffName);
        dest.writeParcelable(this.status, flags);
        dest.writeInt(this.totalCollected);
        dest.writeInt(this.totalOverdue);
        dest.writeInt(this.totaldue);
    }

    public MeetingFallCalendar() {
    }

    protected MeetingFallCalendar(Parcel in) {
        this.accountNo = in.readString();
        this.activationDate = in.createIntArray();
        this.active = in.readByte() != 0;
        this.collectionMeetingCalendar = in
                .readParcelable(CollectionMeetingCalendar.class.getClassLoader());
        this.hierarchy = in.readString();
        this.id = in.readInt();
        this.installmentDue = in.readInt();
        this.name = in.readString();
        this.officeId = in.readInt();
        this.staffId = in.readInt();
        this.staffName = in.readString();
        this.status = in.readParcelable(Status.class.getClassLoader());
        this.totalCollected = in.readInt();
        this.totalOverdue = in.readInt();
        this.totaldue = in.readInt();
    }

    public static final Parcelable.Creator<MeetingFallCalendar> CREATOR = new
            Parcelable.Creator<MeetingFallCalendar>() {
        @Override
        public MeetingFallCalendar createFromParcel(Parcel source) {
            return new MeetingFallCalendar(source);
        }

        @Override
        public MeetingFallCalendar[] newArray(int size) {
            return new MeetingFallCalendar[size];
        }
    };
}

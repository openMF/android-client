/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.group;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.objects.client.Status;
import com.mifos.objects.Timeline;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 28/06/14.
 */
public class Group implements Parcelable {

    private List<Integer> activationDate = new ArrayList<Integer>();
    private Boolean active;
    private String hierarchy;
    private Integer id;
    private String accountNo;
    private int groupLevel;
    private String name;
    private Integer officeId;
    private String officeName;
    private String externalId;
    private Integer staffId;
    private String staffName;
    private Status status;
    private Timeline timeline;

    public String getAccountNo() {
        return this.accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public int getGroupLevel() {
        return this.groupLevel;
    }

    public void setGroupLevel(int groupLevel) {
        this.groupLevel = groupLevel;
    }

    public List<Integer> getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(List<Integer> activationDate) {
        this.activationDate = activationDate;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
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

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.activationDate);
        dest.writeValue(this.active);
        dest.writeString(this.hierarchy);
        dest.writeValue(this.id);
        dest.writeString(this.accountNo);
        dest.writeInt(this.groupLevel);
        dest.writeString(this.name);
        dest.writeValue(this.officeId);
        dest.writeString(this.officeName);
        dest.writeString(this.externalId);
        dest.writeValue(this.staffId);
        dest.writeString(this.staffName);
        dest.writeParcelable(this.status, flags);
        dest.writeParcelable(this.timeline, flags);
    }

    public Group() {
    }

    protected Group(Parcel in) {
        this.activationDate = new ArrayList<Integer>();
        in.readList(this.activationDate, Integer.class.getClassLoader());
        this.active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.hierarchy = in.readString();
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.accountNo = in.readString();
        this.groupLevel = in.readInt();
        this.name = in.readString();
        this.officeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.officeName = in.readString();
        this.externalId = in.readString();
        this.staffId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.staffName = in.readString();
        this.status = in.readParcelable(Status.class.getClassLoader());
        this.timeline = in.readParcelable(Timeline.class.getClassLoader());
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}

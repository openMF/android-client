/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.group;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.mifos.objects.Timeline;
import com.mifos.objects.client.Status;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the Groups Model Table
 * Created by ishankhanna on 28/06/14.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class Group extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    Integer id;

    @Column
    String accountNo;

    @Column
    transient boolean sync;

    @Column
    String name;

    Status status;

    @Column
    Boolean active;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    transient GroupDate groupDate;

    List<Integer> activationDate = new ArrayList<>();

    @Column
    Integer officeId;

    @Column
    String officeName;

    @Column
    int centerId;

    @Column
    String centerName;

    @Column
    Integer staffId;

    @Column
    String staffName;

    @Column
    String hierarchy;

    @Column
    int groupLevel;

    Timeline timeline;

    String externalId;

    public GroupDate getGroupDate() {
        return groupDate;
    }

    public void setGroupDate(GroupDate groupDate) {
        this.groupDate = groupDate;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public Boolean getActive() {
        return active;
    }

    public int getCenterId() {
        return centerId;
    }

    public void setCenterId(int centerId) {
        this.centerId = centerId;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public static Creator<Group> getCREATOR() {
        return CREATOR;
    }

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

    public Boolean isActive() {
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
        this.activationDate = new ArrayList<>();
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

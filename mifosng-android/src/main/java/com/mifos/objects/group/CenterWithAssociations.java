/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.group;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.mifos.objects.client.Status;
import com.mifos.objects.Timeline;
import com.mifos.objects.collectionsheet.CollectionMeetingCalendar;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 28/06/14.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class CenterWithAssociations extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    Integer id;

    @Column
    String accountNo;

    @Column
    String name;

    @Column
    String externalId;

    @Column
    Integer officeId;

    @Column
    String officeName;

    @Column
    Integer staffId;

    @Column
    String staffName;

    @Column
    String hierarchy;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Status status;

    @Column
    Boolean active;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    transient CenterDate centerDate;

    List<Integer> activationDate = new ArrayList<Integer>();
    Timeline timeline;
    List<Group> groupMembers = new ArrayList<Group>();

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    CollectionMeetingCalendar collectionMeetingCalendar = new CollectionMeetingCalendar();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public CenterDate getCenterDate() {

        return centerDate;
    }

    public void setCenterDate(CenterDate centerDate) {
        this.centerDate = centerDate;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
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

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Integer> getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(List<Integer> activationDate) {
        this.activationDate = activationDate;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public List<Group> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<Group> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public CollectionMeetingCalendar getCollectionMeetingCalendar() {
        return collectionMeetingCalendar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.accountNo);
        dest.writeString(this.name);
        dest.writeString(this.externalId);
        dest.writeValue(this.officeId);
        dest.writeString(this.officeName);
        dest.writeValue(this.staffId);
        dest.writeString(this.staffName);
        dest.writeString(this.hierarchy);
        dest.writeParcelable(this.status, flags);
        dest.writeValue(this.active);
        dest.writeList(this.activationDate);
        dest.writeParcelable(this.timeline, flags);
        dest.writeTypedList(groupMembers);
        dest.writeParcelable(this.collectionMeetingCalendar, flags);
    }

    public CenterWithAssociations() {
    }

    protected CenterWithAssociations(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.accountNo = in.readString();
        this.name = in.readString();
        this.externalId = in.readString();
        this.officeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.officeName = in.readString();
        this.staffId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.staffName = in.readString();
        this.hierarchy = in.readString();
        this.status = in.readParcelable(Status.class.getClassLoader());
        this.active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.activationDate = new ArrayList<Integer>();
        in.readList(this.activationDate, Integer.class.getClassLoader());
        this.timeline = in.readParcelable(Timeline.class.getClassLoader());
        this.groupMembers = in.createTypedArrayList(Group.CREATOR);
        this.collectionMeetingCalendar = in.readParcelable(CollectionMeetingCalendar.class
                .getClassLoader());
    }

    public static final Parcelable.Creator<CenterWithAssociations> CREATOR = new Parcelable
            .Creator<CenterWithAssociations>() {
        @Override
        public CenterWithAssociations createFromParcel(Parcel source) {
            return new CenterWithAssociations(source);
        }

        @Override
        public CenterWithAssociations[] newArray(int size) {
            return new CenterWithAssociations[size];
        }
    };
}
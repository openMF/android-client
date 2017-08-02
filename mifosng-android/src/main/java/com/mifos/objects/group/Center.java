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
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * This is Center Model Table
 * Created by ishankhanna on 11/03/14.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class Center extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    Integer id;

    @Column
    transient Boolean sync;

    @Column
    String accountNo;

    @Column
    String name;

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

    Status status;

    @Column
    Boolean active;

    List<Integer> activationDate = new ArrayList<Integer>();

    Timeline timeline;

    String externalId;

    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
    }

    public Boolean getActive() {
        return this.active;
    }

    public String getAccountNo() {
        return this.accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
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

    public Boolean isActive() {
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

    @Override
    public String toString() {
        return "Center{" +
                "id=" + id +
                ", accountNo='" + accountNo + '\'' +
                ", name='" + name + '\'' +
                ", externalId='" + externalId + '\'' +
                ", officeId=" + officeId +
                ", officeName='" + officeName + '\'' +
                ", staffId=" + staffId +
                ", staffName='" + staffName + '\'' +
                ", hierarchy='" + hierarchy + '\'' +
                ", status=" + status +
                ", active=" + active +
                ", activationDate=" + activationDate +
                ", timeline=" + timeline +
                '}';
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
    }

    public Center() {
    }

    protected Center(Parcel in) {
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
    }

    public static final Parcelable.Creator<Center> CREATOR = new Parcelable.Creator<Center>() {
        @Override
        public Center createFromParcel(Parcel source) {
            return new Center(source);
        }

        @Override
        public Center[] newArray(int size) {
            return new Center[size];
        }
    };
}

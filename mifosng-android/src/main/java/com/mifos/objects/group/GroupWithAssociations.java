/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.group;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.objects.Timeline;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 29/06/14.
 */
public class GroupWithAssociations implements Parcelable {

    @SerializedName("id")
    Integer id;

    @SerializedName("accountNo")
    String accountNo;

    @SerializedName("name")
    String name;

    @SerializedName("status")
    Status status;

    @SerializedName("active")
    Boolean active;

    @SerializedName("activationDate")
    List<Integer> activationDate = new ArrayList<>();

    @SerializedName("officeId")
    Integer officeId;

    @SerializedName("officeName")
    String officeName;

    @SerializedName("staffId")
    Integer staffId;

    @SerializedName("staffName")
    String staffName;

    @SerializedName("hierarchy")
    String hierarchy;

    @SerializedName("groupLevel")
    Integer groupLevel;

    @SerializedName("clientMembers")
    List<Client> clientMembers = new ArrayList<>();

    @SerializedName("timeline")
    Timeline timeline;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Integer getGroupLevel() {
        return groupLevel;
    }

    public void setGroupLevel(Integer groupLevel) {
        this.groupLevel = groupLevel;
    }

    public List<Integer> getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(List<Integer> activationDate) {
        this.activationDate = activationDate;
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

    public List<Client> getClientMembers() {
        return clientMembers;
    }

    public void setClientMembers(List<Client> clientMembers) {
        this.clientMembers = clientMembers;
    }

    @Override
    public String toString() {
        return "GroupWithAssociations{" +
                "id=" + id +
                ", accountNo='" + accountNo + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", active=" + active +
                ", activationDate=" + activationDate +
                ", officeId=" + officeId +
                ", officeName='" + officeName + '\'' +
                ", staffId=" + staffId +
                ", staffName='" + staffName + '\'' +
                ", hierarchy='" + hierarchy + '\'' +
                ", groupLevel=" + groupLevel +
                ", clientMembers=" + clientMembers +
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
        dest.writeParcelable(this.status, flags);
        dest.writeValue(this.active);
        dest.writeList(this.activationDate);
        dest.writeValue(this.officeId);
        dest.writeString(this.officeName);
        dest.writeValue(this.staffId);
        dest.writeString(this.staffName);
        dest.writeString(this.hierarchy);
        dest.writeValue(this.groupLevel);
        dest.writeTypedList(this.clientMembers);
        dest.writeParcelable(this.timeline, flags);
    }

    public GroupWithAssociations() {
    }

    protected GroupWithAssociations(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.accountNo = in.readString();
        this.name = in.readString();
        this.status = in.readParcelable(Status.class.getClassLoader());
        this.active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.activationDate = new ArrayList<>();
        in.readList(this.activationDate, Integer.class.getClassLoader());
        this.officeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.officeName = in.readString();
        this.staffId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.staffName = in.readString();
        this.hierarchy = in.readString();
        this.groupLevel = (Integer) in.readValue(Integer.class.getClassLoader());
        this.clientMembers = in.createTypedArrayList(Client.CREATOR);
        this.timeline = in.readParcelable(Timeline.class.getClassLoader());
    }

    public static final Parcelable.Creator<GroupWithAssociations> CREATOR =
            new Parcelable.Creator<GroupWithAssociations>() {
        @Override
        public GroupWithAssociations createFromParcel(Parcel source) {
            return new GroupWithAssociations(source);
        }

        @Override
        public GroupWithAssociations[] newArray(int size) {
            return new GroupWithAssociations[size];
        }
    };
}

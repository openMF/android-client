package com.mifos.objects.templates.loans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayankjindal on 02/10/16.
 */

public class Group implements Parcelable {
    @SerializedName("id")
    Integer id;

    @SerializedName("accountNo")
    Integer accountNo;

    @SerializedName("name")
    String name;

    @SerializedName("externalId")
    Integer externalId;

    @SerializedName("status")
    Status status;

    @SerializedName("active")
    Boolean active;

    @SerializedName("activationDate")
    List<Integer> activationDate;

    @SerializedName("officeId")
    Integer officeId;

    @SerializedName("officeName")
    String officeName;

    @SerializedName("hierarchy")
    String hierarchy;

    @SerializedName("groupLevel")
    Integer groupLevel;

    @SerializedName("timeline")
    GroupTimeline timeline;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(Integer accountNo) {
        this.accountNo = accountNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getExternalId() {
        return externalId;
    }

    public void setExternalId(Integer externalId) {
        this.externalId = externalId;
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

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String  hierarchy) {
        this.hierarchy = hierarchy;
    }

    public Integer getGroupLevel() {
        return groupLevel;
    }

    public void setGroupLevel(Integer groupLevel) {
        this.groupLevel = groupLevel;
    }

    public GroupTimeline getTimeline() {
        return timeline;
    }

    public void setTimeline(GroupTimeline timeline) {
        this.timeline = timeline;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.accountNo);
        dest.writeString(this.name);
        dest.writeValue(this.externalId);
        dest.writeParcelable(this.status, flags);
        dest.writeValue(this.active);
        dest.writeList(this.activationDate);
        dest.writeValue(this.officeId);
        dest.writeString(this.officeName);
        dest.writeString(this.hierarchy);
        dest.writeValue(this.groupLevel);
        dest.writeParcelable(this.timeline, flags);
    }

    public Group() {
    }

    protected Group(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.accountNo = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.externalId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.status = in.readParcelable(Status.class.getClassLoader());
        this.active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.activationDate = new ArrayList<Integer>();
        in.readList(this.activationDate, Integer.class.getClassLoader());
        this.officeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.officeName = in.readString();
        this.hierarchy = in.readString();
        this.groupLevel = (Integer) in.readValue(Integer.class.getClassLoader());
        this.timeline = in.readParcelable(GroupTimeline.class
                .getClassLoader());
    }

    public static final Parcelable.Creator<Group> CREATOR =
            new Parcelable.Creator<Group>() {
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

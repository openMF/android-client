package com.mifos.objects.group;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajan Maurya on 05/02/17.
 */

public class CenterInfo implements Parcelable {

    @SerializedName("activeClients")
    Integer activeClients;

    @SerializedName("activeLoans")
    Integer activeLoans;

    @SerializedName("activeClientLoans")
    Integer activeClientLoans;

    @SerializedName("activeGroupLoans")
    Integer activeGroupLoans;

    @SerializedName("activeBorrowers")
    Integer activeBorrowers;

    @SerializedName("activeClientBorrowers")
    Integer activeClientBorrowers;

    @SerializedName("activeGroupBorrowers")
    Integer activeGroupBorrowers;

    @SerializedName("overdueLoans")
    Integer overdueLoans;

    @SerializedName("overdueClientLoans")
    Integer overdueClientLoans;

    @SerializedName("overdueGroupLoans")
    Integer overdueGroupLoans;

    public Integer getActiveClients() {
        return activeClients;
    }

    public void setActiveClients(Integer activeClients) {
        this.activeClients = activeClients;
    }

    public Integer getActiveLoans() {
        return activeLoans;
    }

    public void setActiveLoans(Integer activeLoans) {
        this.activeLoans = activeLoans;
    }

    public Integer getActiveClientLoans() {
        return activeClientLoans;
    }

    public void setActiveClientLoans(Integer activeClientLoans) {
        this.activeClientLoans = activeClientLoans;
    }

    public Integer getActiveGroupLoans() {
        return activeGroupLoans;
    }

    public void setActiveGroupLoans(Integer activeGroupLoans) {
        this.activeGroupLoans = activeGroupLoans;
    }

    public Integer getActiveBorrowers() {
        return activeBorrowers;
    }

    public void setActiveBorrowers(Integer activeBorrowers) {
        this.activeBorrowers = activeBorrowers;
    }

    public Integer getActiveClientBorrowers() {
        return activeClientBorrowers;
    }

    public void setActiveClientBorrowers(Integer activeClientBorrowers) {
        this.activeClientBorrowers = activeClientBorrowers;
    }

    public Integer getActiveGroupBorrowers() {
        return activeGroupBorrowers;
    }

    public void setActiveGroupBorrowers(Integer activeGroupBorrowers) {
        this.activeGroupBorrowers = activeGroupBorrowers;
    }

    public Integer getOverdueLoans() {
        return overdueLoans;
    }

    public void setOverdueLoans(Integer overdueLoans) {
        this.overdueLoans = overdueLoans;
    }

    public Integer getOverdueClientLoans() {
        return overdueClientLoans;
    }

    public void setOverdueClientLoans(Integer overdueClientLoans) {
        this.overdueClientLoans = overdueClientLoans;
    }

    public Integer getOverdueGroupLoans() {
        return overdueGroupLoans;
    }

    public void setOverdueGroupLoans(Integer overdueGroupLoans) {
        this.overdueGroupLoans = overdueGroupLoans;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.activeClients);
        dest.writeValue(this.activeLoans);
        dest.writeValue(this.activeClientLoans);
        dest.writeValue(this.activeGroupLoans);
        dest.writeValue(this.activeBorrowers);
        dest.writeValue(this.activeClientBorrowers);
        dest.writeValue(this.activeGroupBorrowers);
        dest.writeValue(this.overdueLoans);
        dest.writeValue(this.overdueClientLoans);
        dest.writeValue(this.overdueGroupLoans);
    }

    public CenterInfo() {
    }

    protected CenterInfo(Parcel in) {
        this.activeClients = (Integer) in.readValue(Integer.class.getClassLoader());
        this.activeLoans = (Integer) in.readValue(Integer.class.getClassLoader());
        this.activeClientLoans = (Integer) in.readValue(Integer.class.getClassLoader());
        this.activeGroupLoans = (Integer) in.readValue(Integer.class.getClassLoader());
        this.activeBorrowers = (Integer) in.readValue(Integer.class.getClassLoader());
        this.activeClientBorrowers = (Integer) in.readValue(Integer.class.getClassLoader());
        this.activeGroupBorrowers = (Integer) in.readValue(Integer.class.getClassLoader());
        this.overdueLoans = (Integer) in.readValue(Integer.class.getClassLoader());
        this.overdueClientLoans = (Integer) in.readValue(Integer.class.getClassLoader());
        this.overdueGroupLoans = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<CenterInfo> CREATOR =
            new Parcelable.Creator<CenterInfo>() {
        @Override
        public CenterInfo createFromParcel(Parcel source) {
            return new CenterInfo(source);
        }

        @Override
        public CenterInfo[] newArray(int size) {
            return new CenterInfo[size];
        }
    };
}

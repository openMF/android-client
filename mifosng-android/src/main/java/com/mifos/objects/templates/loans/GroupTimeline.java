package com.mifos.objects.templates.loans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayankjindal on 02/10/16.
 */

public class GroupTimeline implements Parcelable {
    @SerializedName("submittedOnDate")
    List<Integer> submittedOnDate;

    @SerializedName("submittedByUsername")
    String submittedByUsername;

    @SerializedName("submittedByFirstname")
    String submittedByFirstname;

    @SerializedName("submittedByLastname")
    String submittedByLastname;

    @SerializedName("activatedOnDate")
    List<Integer> activatedOnDate;

    @SerializedName("activatedByUsername")
    String activatedByUsername;

    @SerializedName("activatedByFirstname")
    String activatedByFirstname;

    @SerializedName("activatedByLastname")
    String activatedByLastname;


    public List<Integer> getSubmittedOnDate() {
        return submittedOnDate;
    }

    public void setSubmittedOnDate(List<Integer> submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    public String getSubmittedByUsername() {
        return submittedByUsername;
    }

    public void setSubmittedByUsername(String submittedByUsername) {
        this.submittedByUsername = submittedByUsername;
    }

    public String getSubmittedByFirstname() {
        return submittedByFirstname;
    }

    public void setSubmittedByFirstname(String submittedByFirstname) {
        this.submittedByFirstname = submittedByFirstname;
    }

    public String getSubmittedByLastname() {
        return submittedByLastname;
    }

    public void setSubmittedByLastname(String submittedByLastname) {
        this.submittedByLastname = submittedByLastname;
    }

    public List<Integer> getActivatedOnDate() {
        return activatedOnDate;
    }

    public void setActivatedOnDate(List<Integer> activatedOnDate) {
        this.activatedOnDate = activatedOnDate;
    }

    public String getActivatedByUsername() {
        return activatedByUsername;
    }

    public void setActivatedByUsername(String activatedByUsername) {
        this.activatedByUsername = activatedByUsername;
    }

    public String getActivatedByFirstname() {
        return activatedByFirstname;
    }

    public void setActivatedByFirstname(String activatedByFirstname) {
        this.activatedByFirstname = activatedByFirstname;
    }

    public String getActivatedByLastname() {
        return activatedByLastname;
    }

    public void setActivatedByLastname(String activatedByLastname) {
        this.activatedByLastname = activatedByLastname;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.submittedOnDate);
        dest.writeString(this.submittedByUsername);
        dest.writeString(this.submittedByFirstname);
        dest.writeString(this.submittedByLastname);
        dest.writeList(this.activatedOnDate);
        dest.writeString(this.activatedByUsername);
        dest.writeString(this.activatedByFirstname);
        dest.writeString(this.activatedByLastname);
    }

    public GroupTimeline() {
    }

    protected GroupTimeline(Parcel in) {
        this.submittedOnDate = new ArrayList<Integer>();
        in.readList(this.submittedOnDate, Integer.class.getClassLoader());
        this.submittedByUsername = in.readString();
        this.submittedByFirstname = in.readString();
        this.submittedByLastname = in.readString();
        this.activatedOnDate = new ArrayList<Integer>();
        in.readList(this.activatedOnDate, Integer.class.getClassLoader());
        this.activatedByUsername = in.readString();
        this.activatedByFirstname = in.readString();
        this.activatedByLastname = in.readString();
    }

    public static final Parcelable.Creator<GroupTimeline> CREATOR =
            new Parcelable.Creator<GroupTimeline>() {
        @Override
        public GroupTimeline createFromParcel(Parcel source) {
            return new GroupTimeline(source);
        }

        @Override
        public GroupTimeline[] newArray(int size) {
            return new GroupTimeline[size];
        }
    };
}

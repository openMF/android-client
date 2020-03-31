/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.savings;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class Timeline implements Parcelable {

    List<Integer> submittedOnDate = new ArrayList<Integer>();

    String submittedByUsername;

    String submittedByFirstname;

    String submittedByLastname;
    List<Integer> approvedOnDate = new ArrayList<Integer>();

    String approvedByUsername;

    String approvedByFirstname;

    String approvedByLastname;

    List<Integer> activatedOnDate = new ArrayList<Integer>();

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

    public List<Integer> getApprovedOnDate() {
        return approvedOnDate;
    }

    public void setApprovedOnDate(List<Integer> approvedOnDate) {
        this.approvedOnDate = approvedOnDate;
    }

    public String getApprovedByUsername() {
        return approvedByUsername;
    }

    public void setApprovedByUsername(String approvedByUsername) {
        this.approvedByUsername = approvedByUsername;
    }

    public String getApprovedByFirstname() {
        return approvedByFirstname;
    }

    public void setApprovedByFirstname(String approvedByFirstname) {
        this.approvedByFirstname = approvedByFirstname;
    }

    public String getApprovedByLastname() {
        return approvedByLastname;
    }

    public void setApprovedByLastname(String approvedByLastname) {
        this.approvedByLastname = approvedByLastname;
    }

    public List<Integer> getActivatedOnDate() {
        return activatedOnDate;
    }

    public void setActivatedOnDate(List<Integer> activatedOnDate) {
        this.activatedOnDate = activatedOnDate;
    }

    @Override
    public String toString() {
        return "Timeline{" +
                "submittedOnDate=" + submittedOnDate +
                ", submittedByUsername='" + submittedByUsername + '\'' +
                ", submittedByFirstname='" + submittedByFirstname + '\'' +
                ", submittedByLastname='" + submittedByLastname + '\'' +
                ", approvedOnDate=" + approvedOnDate +
                ", approvedByUsername='" + approvedByUsername + '\'' +
                ", approvedByFirstname='" + approvedByFirstname + '\'' +
                ", approvedByLastname='" + approvedByLastname + '\'' +
                ", activatedOnDate=" + activatedOnDate +
                '}';
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
        dest.writeList(this.approvedOnDate);
        dest.writeString(this.approvedByUsername);
        dest.writeString(this.approvedByFirstname);
        dest.writeString(this.approvedByLastname);
        dest.writeList(this.activatedOnDate);
    }

    public Timeline() {
    }

    protected Timeline(Parcel in) {
        this.submittedOnDate = new ArrayList<Integer>();
        in.readList(this.submittedOnDate, Integer.class.getClassLoader());
        this.submittedByUsername = in.readString();
        this.submittedByFirstname = in.readString();
        this.submittedByLastname = in.readString();
        this.approvedOnDate = new ArrayList<Integer>();
        in.readList(this.approvedOnDate, Integer.class.getClassLoader());
        this.approvedByUsername = in.readString();
        this.approvedByFirstname = in.readString();
        this.approvedByLastname = in.readString();
        this.activatedOnDate = new ArrayList<Integer>();
        in.readList(this.activatedOnDate, Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Timeline> CREATOR = new Parcelable.Creator<Timeline>() {
        @Override
        public Timeline createFromParcel(Parcel source) {
            return new Timeline(source);
        }

        @Override
        public Timeline[] newArray(int size) {
            return new Timeline[size];
        }
    };
}

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.loan;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * This Model Time Object of LoanWithAssociations.
 *
 * Here
 */
@Table(database = MifosDatabase.class, name = "LoanWithAssociations_Timeline")
@ModelContainer
public class Timeline extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    transient Integer loanId;

    List<Integer> submittedOnDate = new ArrayList<Integer>();

    String submittedByUsername;

    String submittedByFirstname;

    String submittedByLastname;

    List<Integer> approvedOnDate = new ArrayList<Integer>();

    String approvedByUsername;

    String approvedByFirstname;

    String approvedByLastname;

    List<Integer> expectedDisbursementDate = new ArrayList<Integer>();

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    ActualDisbursementDate actualDisburseDate;

    List<Integer> actualDisbursementDate = new ArrayList<Integer>();

    String disbursedByUsername;

    String disbursedByFirstname;

    String disbursedByLastname;

    List<Integer> closedOnDate = new ArrayList<Integer>();

    List<Integer> expectedMaturityDate = new ArrayList<Integer>();

    public ActualDisbursementDate getActualDisburseDate() {
        return actualDisburseDate;
    }

    public void setActualDisburseDate(ActualDisbursementDate actualDisburseDate) {
        this.actualDisburseDate = actualDisburseDate;
    }

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

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

    public List<Integer> getExpectedDisbursementDate() {
        return expectedDisbursementDate;
    }

    public void setExpectedDisbursementDate(List<Integer> expectedDisbursementDate) {
        this.expectedDisbursementDate = expectedDisbursementDate;
    }

    public List<Integer> getActualDisbursementDate() {
        return actualDisbursementDate;
    }

    public void setActualDisbursementDate(List<Integer> actualDisbursementDate) {
        this.actualDisbursementDate = actualDisbursementDate;
    }

    public String getDisbursedByUsername() {
        return disbursedByUsername;
    }

    public void setDisbursedByUsername(String disbursedByUsername) {
        this.disbursedByUsername = disbursedByUsername;
    }

    public String getDisbursedByFirstname() {
        return disbursedByFirstname;
    }

    public void setDisbursedByFirstname(String disbursedByFirstname) {
        this.disbursedByFirstname = disbursedByFirstname;
    }

    public String getDisbursedByLastname() {
        return disbursedByLastname;
    }

    public void setDisbursedByLastname(String disbursedByLastname) {
        this.disbursedByLastname = disbursedByLastname;
    }

    public List<Integer> getClosedOnDate() {
        return closedOnDate;
    }

    public void setClosedOnDate(List<Integer> closedOnDate) {
        this.closedOnDate = closedOnDate;
    }

    public List<Integer> getExpectedMaturityDate() {
        return expectedMaturityDate;
    }

    public void setExpectedMaturityDate(List<Integer> expectedMaturityDate) {
        this.expectedMaturityDate = expectedMaturityDate;
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
        dest.writeList(this.expectedDisbursementDate);
        dest.writeParcelable(this.actualDisburseDate, flags);
        dest.writeList(this.actualDisbursementDate);
        dest.writeString(this.disbursedByUsername);
        dest.writeString(this.disbursedByFirstname);
        dest.writeString(this.disbursedByLastname);
        dest.writeList(this.closedOnDate);
        dest.writeList(this.expectedMaturityDate);
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
        this.expectedDisbursementDate = new ArrayList<Integer>();
        in.readList(this.expectedDisbursementDate, Integer.class.getClassLoader());
        this.actualDisburseDate = in.readParcelable(ActualDisbursementDate.class.getClassLoader());
        this.actualDisbursementDate = new ArrayList<Integer>();
        in.readList(this.actualDisbursementDate, Integer.class.getClassLoader());
        this.disbursedByUsername = in.readString();
        this.disbursedByFirstname = in.readString();
        this.disbursedByLastname = in.readString();
        this.closedOnDate = new ArrayList<Integer>();
        in.readList(this.closedOnDate, Integer.class.getClassLoader());
        this.expectedMaturityDate = new ArrayList<Integer>();
        in.readList(this.expectedMaturityDate, Integer.class.getClassLoader());
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

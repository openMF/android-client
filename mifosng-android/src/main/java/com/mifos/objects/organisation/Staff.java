/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.organisation;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by ishankhanna on 14/07/14.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class Staff extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    Integer id;

    @Column
    String firstname;

    @Column
    String lastname;

    @Column
    String mobileNo;

    @Column
    String displayName;

    @Column
    Integer officeId;

    @Column
    String officeName;

    @Column
    Boolean isLoanOfficer;

    @Column
    Boolean isActive;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public Boolean getIsLoanOfficer() {
        return isLoanOfficer;
    }

    public void setIsLoanOfficer(Boolean isLoanOfficer) {
        this.isLoanOfficer = isLoanOfficer;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.firstname);
        dest.writeString(this.lastname);
        dest.writeString(this.mobileNo);
        dest.writeString(this.displayName);
        dest.writeValue(this.officeId);
        dest.writeString(this.officeName);
        dest.writeValue(this.isLoanOfficer);
        dest.writeValue(this.isActive);
    }

    public Staff() {
    }

    protected Staff(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.firstname = in.readString();
        this.lastname = in.readString();
        this.mobileNo = in.readString();
        this.displayName = in.readString();
        this.officeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.officeName = in.readString();
        this.isLoanOfficer = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Staff> CREATOR = new Parcelable.Creator<Staff>() {
        @Override
        public Staff createFromParcel(Parcel source) {
            return new Staff(source);
        }

        @Override
        public Staff[] newArray(int size) {
            return new Staff[size];
        }
    };
}

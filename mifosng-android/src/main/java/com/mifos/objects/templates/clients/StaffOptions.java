package com.mifos.objects.templates.clients;

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;


/**
 * Created by rajan on 13/3/16.
 */
@Table(database = MifosDatabase.class, name = "ClientTemplateStaffOptions")
@ModelContainer
public class StaffOptions extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    int id;

    @Column
    String firstname;

    @Column
    String lastname;

    @Column
    String displayName;

    @Column
    int officeId;

    @Column
    String officeName;

    @SerializedName("isLoanOfficer")
    @Column
    boolean isLoan_officer;

    @SerializedName("isActive")
    @Column
    boolean is_Active;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public boolean isLoanOfficer() {
        return isLoan_officer;
    }

    public void setIsLoanOfficer(boolean isLoanOfficer) {
        this.isLoan_officer = isLoanOfficer;
    }

    public boolean isActive() {
        return is_Active;
    }

    public void setIsActive(boolean isActive) {
        this.is_Active = isActive;
    }

    @Override
    public String toString() {
        return "StaffOptions{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", displayName='" + displayName + '\'' +
                ", officeId=" + officeId +
                ", officeName='" + officeName + '\'' +
                ", isLoanOfficer=" + isLoan_officer +
                ", isActive=" + is_Active +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.firstname);
        dest.writeString(this.lastname);
        dest.writeString(this.displayName);
        dest.writeInt(this.officeId);
        dest.writeString(this.officeName);
        dest.writeByte(this.isLoan_officer ? (byte) 1 : (byte) 0);
        dest.writeByte(this.is_Active ? (byte) 1 : (byte) 0);
    }

    public StaffOptions() {
    }

    protected StaffOptions(Parcel in) {
        this.id = in.readInt();
        this.firstname = in.readString();
        this.lastname = in.readString();
        this.displayName = in.readString();
        this.officeId = in.readInt();
        this.officeName = in.readString();
        this.isLoan_officer = in.readByte() != 0;
        this.is_Active = in.readByte() != 0;
    }

    public static final Parcelable.Creator<StaffOptions> CREATOR = new Parcelable
            .Creator<StaffOptions>() {
        @Override
        public StaffOptions createFromParcel(Parcel source) {
            return new StaffOptions(source);
        }

        @Override
        public StaffOptions[] newArray(int size) {
            return new StaffOptions[size];
        }
    };
}

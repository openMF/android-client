package com.mifos.objects.client;
/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by ADMIN on 16-Jun-15.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class ClientPayload extends MifosBaseModel implements Parcelable {


    @PrimaryKey(autoincrement = true)
    transient int id;

    @Column
    transient String errorMessage;

    @Column
    String firstname;

    @Column
    String lastname;

    @Column
    String middlename;

    @Column
    int officeId;

    @Column
    int staffId;

    @Column
    int genderId;

    @Column
    boolean active;

    @Column
    String activationDate;

    @Column
    String submittedOnDate;

    @Column
    String dateOfBirth;

    @Column
    String mobileNo;

    @Column
    String externalId;

    @Column
    int clientTypeId;

    @Column
    int clientClassificationId;

    @Column
    String dateFormat = "dd MMMM YYYY";

    @Column
    String locale = "en";

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getClientClassificationId() {
        return clientClassificationId;
    }

    public void setClientClassificationId(int clientClassificationId) {
        this.clientClassificationId = clientClassificationId;
    }

    public int getGenderId() {
        return genderId;
    }

    public void setGenderId(int genderId) {
        this.genderId = genderId;
    }

    public int getClientTypeId() {
        return clientTypeId;
    }

    public void setClientTypeId(int clientTypeId) {
        this.clientTypeId = clientTypeId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

    public String getSubmittedOnDate() {
        return submittedOnDate;
    }

    public void setSubmittedOnDate(String submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
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
        dest.writeString(this.middlename);
        dest.writeInt(this.officeId);
        dest.writeInt(this.staffId);
        dest.writeInt(this.genderId);
        dest.writeByte(this.active ? (byte) 1 : (byte) 0);
        dest.writeString(this.activationDate);
        dest.writeString(this.submittedOnDate);
        dest.writeString(this.dateOfBirth);
        dest.writeString(this.mobileNo);
        dest.writeString(this.externalId);
        dest.writeInt(this.clientTypeId);
        dest.writeInt(this.clientClassificationId);
        dest.writeString(this.dateFormat);
        dest.writeString(this.locale);
    }

    public ClientPayload() {
    }

    protected ClientPayload(Parcel in) {
        this.id = in.readInt();
        this.firstname = in.readString();
        this.lastname = in.readString();
        this.middlename = in.readString();
        this.officeId = in.readInt();
        this.staffId = in.readInt();
        this.genderId = in.readInt();
        this.active = in.readByte() != 0;
        this.activationDate = in.readString();
        this.submittedOnDate = in.readString();
        this.dateOfBirth = in.readString();
        this.mobileNo = in.readString();
        this.externalId = in.readString();
        this.clientTypeId = in.readInt();
        this.clientClassificationId = in.readInt();
        this.dateFormat = in.readString();
        this.locale = in.readString();
    }

    public static final Parcelable.Creator<ClientPayload> CREATOR = new Parcelable
            .Creator<ClientPayload>() {
        @Override
        public ClientPayload createFromParcel(Parcel source) {
            return new ClientPayload(source);
        }

        @Override
        public ClientPayload[] newArray(int size) {
            return new ClientPayload[size];
        }
    };
}


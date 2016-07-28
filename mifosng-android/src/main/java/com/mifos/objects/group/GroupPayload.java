/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.group;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by nellyk on 1/22/2016.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class GroupPayload extends MifosBaseModel implements Parcelable {

    @PrimaryKey(autoincrement = true)
    transient int id;

    @Column
    transient String errorMessage;

    @Column
    int officeId;

    @Column
    boolean active;

    @Column
    String activationDate;

    @Column
    String submittedOnDate;

    @Column
    String externalId;

    @Column
    String name;

    @Column
    String locale;

    @Column
    String dateFormat;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubmittedOnDate() {
        return submittedOnDate;
    }

    public void setSubmittedOnDate(String submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
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

    public String setSubmissionDate() {
        return submittedOnDate;
    }

    public void setSubmissionDate(String submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.officeId);
        dest.writeByte(this.active ? (byte) 1 : (byte) 0);
        dest.writeString(this.activationDate);
        dest.writeString(this.submittedOnDate);
        dest.writeString(this.externalId);
        dest.writeString(this.name);
        dest.writeString(this.locale);
        dest.writeString(this.dateFormat);
    }

    public GroupPayload() {
    }

    protected GroupPayload(Parcel in) {
        this.officeId = in.readInt();
        this.active = in.readByte() != 0;
        this.activationDate = in.readString();
        this.submittedOnDate = in.readString();
        this.externalId = in.readString();
        this.name = in.readString();
        this.locale = in.readString();
        this.dateFormat = in.readString();
    }

    public static final Parcelable.Creator<GroupPayload> CREATOR =
            new Parcelable.Creator<GroupPayload>() {
        @Override
        public GroupPayload createFromParcel(Parcel source) {
            return new GroupPayload(source);
        }

        @Override
        public GroupPayload[] newArray(int size) {
            return new GroupPayload[size];
        }
    };
}

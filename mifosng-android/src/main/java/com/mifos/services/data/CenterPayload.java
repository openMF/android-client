/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.services.data;

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
public class CenterPayload extends MifosBaseModel implements Parcelable {

    @PrimaryKey(autoincrement = true)
    transient int id;

    @Column
    transient String errorMessage;

    @Column
    String dateFormat;

    @Column
    String locale;

    @Column
    private String name;

    @Column
    private int officeId;

    @Column
    private boolean active;

    @Column
    private String activationDate;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dateFormat);
        dest.writeString(this.locale);
        dest.writeString(this.name);
        dest.writeValue(this.officeId);
        dest.writeValue(this.active);
        dest.writeString(this.activationDate);
    }

    public CenterPayload() {
    }

    protected CenterPayload(Parcel in) {
        this.dateFormat = in.readString();
        this.locale = in.readString();
        this.name = in.readString();
        this.officeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.activationDate = in.readString();
    }

    public static final Parcelable.Creator<CenterPayload> CREATOR =
            new Parcelable.Creator<CenterPayload>() {
                @Override
                public CenterPayload createFromParcel(Parcel source) {
                    return new CenterPayload(source);
                }

                @Override
                public CenterPayload[] newArray(int size) {
                    return new CenterPayload[size];
                }
            };


}

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
@Table(database = MifosDatabase.class, name = "ClientTemplateOptions")
@ModelContainer
public class Options  extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    boolean genderOptions;

    @PrimaryKey
    boolean clientTypeOptions;

    @PrimaryKey
    boolean clientClassificationOptions;

    @PrimaryKey
    boolean clientLegalFormOptions;

    @Column
    int id;

    @Column
    String name;

    @Column
    int position;

    @Column
    String description;

    @SerializedName("isActive")
    @Column
    private boolean is_Active;

    public boolean isClientLegalFormOptions() {
        return clientLegalFormOptions;
    }

    public void setClientLegalFormOptions(boolean clientLegalFormOptions) {
        this.clientLegalFormOptions = clientLegalFormOptions;
    }

    public boolean isGenderOptions() {
        return genderOptions;
    }

    public void setGenderOptions(boolean genderOptions) {
        this.genderOptions = genderOptions;
    }

    public boolean isClientTypeOptions() {
        return clientTypeOptions;
    }

    public void setClientTypeOptions(boolean clientTypeOptions) {
        this.clientTypeOptions = clientTypeOptions;
    }

    public boolean isClientClassificationOptions() {
        return clientClassificationOptions;
    }

    public void setClientClassificationOptions(boolean clientClassificationOptions) {
        this.clientClassificationOptions = clientClassificationOptions;
    }

    public boolean is_Active() {
        return is_Active;
    }

    public void setIs_Active(boolean is_Active) {
        this.is_Active = is_Active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return is_Active;
    }

    public void setIsActive(boolean isActive) {
        this.is_Active = isActive;
    }

    @Override
    public String toString() {
        return "Options{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", description='" + description + '\'' +
                ", isActive=" + is_Active +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.genderOptions ? (byte) 1 : (byte) 0);
        dest.writeByte(this.clientTypeOptions ? (byte) 1 : (byte) 0);
        dest.writeByte(this.clientClassificationOptions ? (byte) 1 : (byte) 0);
        dest.writeByte(this.clientLegalFormOptions ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.position);
        dest.writeString(this.description);
        dest.writeByte(this.is_Active ? (byte) 1 : (byte) 0);
    }

    public Options() {
    }

    protected Options(Parcel in) {
        this.genderOptions = in.readByte() != 0;
        this.clientTypeOptions = in.readByte() != 0;
        this.clientClassificationOptions = in.readByte() != 0;
        this.clientLegalFormOptions = in.readByte() != 0;
        this.id = in.readInt();
        this.name = in.readString();
        this.position = in.readInt();
        this.description = in.readString();
        this.is_Active = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Options> CREATOR = new Parcelable.Creator<Options>() {
        @Override
        public Options createFromParcel(Parcel source) {
            return new Options(source);
        }

        @Override
        public Options[] newArray(int size) {
            return new Options[size];
        }
    };
}

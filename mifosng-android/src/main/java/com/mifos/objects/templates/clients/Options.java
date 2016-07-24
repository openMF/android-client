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

    @Column
    String genderOptions;

    @Column
    String clientTypeOptions;

    @Column
    String clientClassificationOptions;

    @PrimaryKey
    int id;

    @Column
    String name;

    @Column
    int position;

    @Column
    String description;

    @SerializedName("isActive")
    @Column
    boolean activeStatus;

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

    public String getGenderOptions() {
        return this.genderOptions;
    }

    public void setGenderOptions(String genderOptions) {
        this.genderOptions = genderOptions;
    }

    public String getClientTypeOptions() {
        return this.clientTypeOptions;
    }

    public void setClientTypeOptions(String clientTypeOptions) {
        this.clientTypeOptions = clientTypeOptions;
    }

    public String getClientClassificationOptions() {
        return this.clientClassificationOptions;
    }

    public void setClientClassificationOptions(String clientClassificationOptions) {
        this.clientClassificationOptions = clientClassificationOptions;
    }

    public boolean isActiveStatus() {
        return this.activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    @Override
    public String toString() {
        return "Options{" +
                "genderOptions='" + genderOptions + '\'' +
                ", clientTypeOptions='" + clientTypeOptions + '\'' +
                ", clientClassificationOptions='" + clientClassificationOptions + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", description='" + description + '\'' +
                ", activeStatus=" + activeStatus +
                '}';
    }


    public Options() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.genderOptions);
        dest.writeString(this.clientTypeOptions);
        dest.writeString(this.clientClassificationOptions);
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.position);
        dest.writeString(this.description);
        dest.writeByte(activeStatus ? (byte) 1 : (byte) 0);
    }

    protected Options(Parcel in) {
        this.genderOptions = in.readString();
        this.clientTypeOptions = in.readString();
        this.clientClassificationOptions = in.readString();
        this.id = in.readInt();
        this.name = in.readString();
        this.position = in.readInt();
        this.description = in.readString();
        this.activeStatus = in.readByte() != 0;
    }

    public static final Creator<Options> CREATOR = new Creator<Options>() {
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

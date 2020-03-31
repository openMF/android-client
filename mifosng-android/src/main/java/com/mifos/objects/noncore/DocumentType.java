/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.noncore;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ishankhanna on 03/07/14.
 */
public class DocumentType implements Parcelable {

    @SerializedName("id")
    Integer id;

    @SerializedName("name")
    String name;

    @SerializedName("active")
    Boolean active;

    @SerializedName("mandatory")
    Boolean mandatory;

    @SerializedName("description")
    String description;

    @SerializedName("position")
    Integer position;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public DocumentType() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeValue(this.active);
        dest.writeValue(this.mandatory);
        dest.writeString(this.description);
        dest.writeValue(this.position);
    }

    protected DocumentType(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mandatory = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.description = in.readString();
        this.position = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<DocumentType> CREATOR = new Creator<DocumentType>() {
        @Override
        public DocumentType createFromParcel(Parcel source) {
            return new DocumentType(source);
        }

        @Override
        public DocumentType[] newArray(int size) {
            return new DocumentType[size];
        }
    };
}

package com.mifos.objects.noncore;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajan Maurya on 01/10/16.
 */

public class IdentifierType implements Parcelable {

    @SerializedName("id")
    Integer id;

    @SerializedName("name")
    String name;

    @SerializedName("position")
    Integer position;

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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeValue(this.position);
    }

    public IdentifierType() {
    }

    protected IdentifierType(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.position = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<IdentifierType> CREATOR = new Parcelable
            .Creator<IdentifierType>() {
        @Override
        public IdentifierType createFromParcel(Parcel source) {
            return new IdentifierType(source);
        }

        @Override
        public IdentifierType[] newArray(int size) {
            return new IdentifierType[size];
        }
    };
}

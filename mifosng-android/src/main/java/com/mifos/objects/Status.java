/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class Status implements Parcelable {

    private static final String STATUS_ACTIVE = "Active";

    private int id;
    private String code;
    private String value;

    // Helper method to check if status is Active
    public static boolean isActive(String value) {
        return value.equalsIgnoreCase(STATUS_ACTIVE);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Status{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.code);
        dest.writeString(this.value);
    }

    public Status() {
    }

    protected Status(Parcel in) {
        this.id = in.readInt();
        this.code = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<Status> CREATOR = new Parcelable.Creator<Status>() {
        @Override
        public Status createFromParcel(Parcel source) {
            return new Status(source);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[size];
        }
    };
}

package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tarun on 21-07-2017.
 */

public class AttendanceTypeOption implements Parcelable {

    private int id;

    private String code;

    private String value;

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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.code);
        dest.writeString(this.value);
    }

    public AttendanceTypeOption() {
    }

    protected AttendanceTypeOption(Parcel in) {
        this.id = in.readInt();
        this.code = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<AttendanceTypeOption> CREATOR =
            new Parcelable.Creator<AttendanceTypeOption>() {
        @Override
        public AttendanceTypeOption createFromParcel(Parcel source) {
            return new AttendanceTypeOption(source);
        }

        @Override
        public AttendanceTypeOption[] newArray(int size) {
            return new AttendanceTypeOption[size];
        }
    };
}

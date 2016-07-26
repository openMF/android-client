/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.loan;

import android.os.Parcel;
import android.os.Parcelable;

public class InterestRateFrequencyType implements Parcelable {

    private Integer id;
    private String code;
    private String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
        dest.writeValue(this.id);
        dest.writeString(this.code);
        dest.writeString(this.value);
    }

    public InterestRateFrequencyType() {
    }

    protected InterestRateFrequencyType(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.code = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<InterestRateFrequencyType> CREATOR = new Parcelable
            .Creator<InterestRateFrequencyType>() {
        @Override
        public InterestRateFrequencyType createFromParcel(Parcel source) {
            return new InterestRateFrequencyType(source);
        }

        @Override
        public InterestRateFrequencyType[] newArray(int size) {
            return new InterestRateFrequencyType[size];
        }
    };
}

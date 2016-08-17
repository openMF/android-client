/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.savings;

import android.os.Parcel;
import android.os.Parcelable;

public class InterestCalculationDaysInYearType implements Parcelable {

    Integer id;
    String code;
    String value;

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
    public String toString() {
        return "InterestCalculationDaysInYearType{" +
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
        dest.writeValue(this.id);
        dest.writeString(this.code);
        dest.writeString(this.value);
    }

    public InterestCalculationDaysInYearType() {
    }

    protected InterestCalculationDaysInYearType(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.code = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<InterestCalculationDaysInYearType> CREATOR =
            new Parcelable.Creator<InterestCalculationDaysInYearType>() {
        @Override
        public InterestCalculationDaysInYearType createFromParcel(Parcel source) {
            return new InterestCalculationDaysInYearType(source);
        }

        @Override
        public InterestCalculationDaysInYearType[] newArray(int size) {
            return new InterestCalculationDaysInYearType[size];
        }
    };
}

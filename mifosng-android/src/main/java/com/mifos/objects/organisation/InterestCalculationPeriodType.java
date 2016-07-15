package com.mifos.objects.organisation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajan Maurya on 15/07/16.
 */
public class InterestCalculationPeriodType implements Parcelable {

    @SerializedName("id")
    Integer id;

    @SerializedName("code")
    String code;

    @SerializedName("value")
    String value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "InterestCalculationPeriodType{" +
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

    public InterestCalculationPeriodType() {
    }

    protected InterestCalculationPeriodType(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.code = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<InterestCalculationPeriodType> CREATOR =
            new Parcelable.Creator<InterestCalculationPeriodType>() {
        @Override
        public InterestCalculationPeriodType createFromParcel(Parcel source) {
            return new InterestCalculationPeriodType(source);
        }

        @Override
        public InterestCalculationPeriodType[] newArray(int size) {
            return new InterestCalculationPeriodType[size];
        }
    };
}

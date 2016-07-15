package com.mifos.objects.organisation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajan Maurya on 15/07/16.
 */
public class RepaymentFrequencyType implements Parcelable {

    @SerializedName("id")
    Integer id;

    @SerializedName("code")
    String code;

    @SerializedName("value")
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepaymentFrequencyType)) return false;

        RepaymentFrequencyType that = (RepaymentFrequencyType) o;

        if (!getId().equals(that.getId())) return false;
        if (!getCode().equals(that.getCode())) return false;
        return getValue().equals(that.getValue());

    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getCode().hashCode();
        result = 31 * result + getValue().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RepaymentFrequencyType{" +
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

    public RepaymentFrequencyType() {
    }

    protected RepaymentFrequencyType(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.code = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<RepaymentFrequencyType> CREATOR =
            new Parcelable.Creator<RepaymentFrequencyType>() {
        @Override
        public RepaymentFrequencyType createFromParcel(Parcel source) {
            return new RepaymentFrequencyType(source);
        }

        @Override
        public RepaymentFrequencyType[] newArray(int size) {
            return new RepaymentFrequencyType[size];
        }
    };
}

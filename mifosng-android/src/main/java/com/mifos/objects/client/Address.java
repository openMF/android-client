package com.mifos.objects.client;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajan Maurya on 15/12/16.
 */

public class Address implements Parcelable {

    @SerializedName("addressTypeId")
    Integer addressTypeId;

    @SerializedName("isActive")
    Boolean isActive;

    @SerializedName("street")
    String street;

    @SerializedName("stateProvinceId")
    Integer stateProvinceId;

    @SerializedName("countryId")
    Integer countryId;

    public Integer getAddressTypeId() {
        return addressTypeId;
    }

    public void setAddressTypeId(Integer addressTypeId) {
        this.addressTypeId = addressTypeId;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getStateProvinceId() {
        return stateProvinceId;
    }

    public void setStateProvinceId(Integer stateProvinceId) {
        this.stateProvinceId = stateProvinceId;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressTypeId=" + addressTypeId +
                ", isActive=" + isActive +
                ", street='" + street + '\'' +
                ", stateProvinceId=" + stateProvinceId +
                ", countryId=" + countryId +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.addressTypeId);
        dest.writeValue(this.isActive);
        dest.writeString(this.street);
        dest.writeValue(this.stateProvinceId);
        dest.writeValue(this.countryId);
    }

    public Address() {
    }

    protected Address(Parcel in) {
        this.addressTypeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.street = in.readString();
        this.stateProvinceId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.countryId = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}

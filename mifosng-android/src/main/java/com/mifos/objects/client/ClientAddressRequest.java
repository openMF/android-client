/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.client;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ClientAddressRequest implements Parcelable {

    @SerializedName("place_id")
    String placeId;

    @SerializedName("place_address")
    String placeAddress;

    @SerializedName("latitude")
    Double latitude;

    @SerializedName("longitude")
    Double longitude;

    // Defaults
    String dateFormat = "dd MMMM YYYY";
    String locale = "en";

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        return "ClientAddressRequest{" +
                "placeId='" + placeId + '\'' +
                ", placeAddress='" + placeAddress + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", dateFormat='" + dateFormat + '\'' +
                ", locale='" + locale + '\'' +
                '}';
    }

    public ClientAddressRequest(String placeId, String placeAddress, Double latitude,
                                Double longitude) {
        this.placeId = placeId;
        this.placeAddress = placeAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.placeId);
        dest.writeString(this.placeAddress);
        dest.writeValue(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.dateFormat);
        dest.writeString(this.locale);
    }

    public ClientAddressRequest() {
    }

    protected ClientAddressRequest(Parcel in) {
        this.placeId = in.readString();
        this.placeAddress = in.readString();
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = in.readDouble();
        this.dateFormat = in.readString();
        this.locale = in.readString();
    }

    public static final Parcelable.Creator<ClientAddressRequest> CREATOR =
            new Parcelable.Creator<ClientAddressRequest>() {
        @Override
        public ClientAddressRequest createFromParcel(Parcel source) {
            return new ClientAddressRequest(source);
        }

        @Override
        public ClientAddressRequest[] newArray(int size) {
            return new ClientAddressRequest[size];
        }
    };
}

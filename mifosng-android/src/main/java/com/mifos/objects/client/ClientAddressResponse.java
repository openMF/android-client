/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.client;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ClientAddressResponse implements Parcelable {

    @SerializedName("id")
    Integer id;

    @SerializedName("client_id")
    Integer clientId;

    @SerializedName("latitude")
    Double latitude;

    @SerializedName("longitude")
    Double longitude;

    @SerializedName("place_address")
    String placeAddress;

    @SerializedName("place_id")
    String placeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.clientId);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
        dest.writeString(this.placeAddress);
        dest.writeString(this.placeId);
    }

    public ClientAddressResponse() {
    }

    protected ClientAddressResponse(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.clientId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.placeAddress = in.readString();
        this.placeId = in.readString();
    }

    public static final Parcelable.Creator<ClientAddressResponse> CREATOR =
            new Parcelable.Creator<ClientAddressResponse>() {
        @Override
        public ClientAddressResponse createFromParcel(Parcel source) {
            return new ClientAddressResponse(source);
        }

        @Override
        public ClientAddressResponse[] newArray(int size) {
            return new ClientAddressResponse[size];
        }
    };
}

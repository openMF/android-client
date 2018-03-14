package com.mifos.objects.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajan Maurya on 24/01/17.
 */

public class UserLatLng implements Parcelable {

    @SerializedName("lat")
    Double lat;

    @SerializedName("lng")
    Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.lat);
        dest.writeValue(this.lng);
    }

    public UserLatLng(Double latitude, Double longitude) {
        this.lat = latitude;
        this.lng = longitude;
    }

    public UserLatLng() {
    }

    protected UserLatLng(Parcel in) {
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.lng = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<UserLatLng> CREATOR =
            new Parcelable.Creator<UserLatLng>() {
        @Override
        public UserLatLng createFromParcel(Parcel source) {
            return new UserLatLng(source);
        }

        @Override
        public UserLatLng[] newArray(int size) {
            return new UserLatLng[size];
        }
    };
}

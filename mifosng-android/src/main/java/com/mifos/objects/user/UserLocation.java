package com.mifos.objects.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajan Maurya on 24/01/17.
 */

public class UserLocation implements Parcelable {

    @SerializedName("user_id")
    Integer userId;

    @SerializedName("latlng")
    String latlng;

    @SerializedName("start_time")
    String startTime;

    @SerializedName("stop_time")
    String stopTime;

    @SerializedName("date")
    String date;

    String dateFormat = "dd MMMM yyyy HH:mm";

    String locale = "en";

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public UserLocation() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.userId);
        dest.writeString(this.latlng);
        dest.writeString(this.startTime);
        dest.writeString(this.stopTime);
        dest.writeString(this.date);
        dest.writeString(this.dateFormat);
        dest.writeString(this.locale);
    }

    protected UserLocation(Parcel in) {
        this.userId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.latlng = in.readString();
        this.startTime = in.readString();
        this.stopTime = in.readString();
        this.date = in.readString();
        this.dateFormat = in.readString();
        this.locale = in.readString();
    }

    public static final Creator<UserLocation> CREATOR = new Creator<UserLocation>() {
        @Override
        public UserLocation createFromParcel(Parcel source) {
            return new UserLocation(source);
        }

        @Override
        public UserLocation[] newArray(int size) {
            return new UserLocation[size];
        }
    };
}

package com.mifos.objects.client;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajan Maurya on 09/02/17.
 */

public class ActivatePayload implements Parcelable {

    @SerializedName("activationDate")
    String activationDate;

    @SerializedName("dateFormat")
    String dateFormat = "dd MMMM YYYY";

    @SerializedName("locale")
    String locale = "en";

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.activationDate);
        dest.writeString(this.dateFormat);
        dest.writeString(this.locale);
    }

    public ActivatePayload(String activationDate) {
        this.activationDate = activationDate;
    }

    public ActivatePayload() {
    }

    protected ActivatePayload(Parcel in) {
        this.activationDate = in.readString();
        this.dateFormat = in.readString();
        this.locale = in.readString();
    }

    public static final Parcelable.Creator<ActivatePayload> CREATOR = new Parcelable
            .Creator<ActivatePayload>() {
        @Override
        public ActivatePayload createFromParcel(Parcel source) {
            return new ActivatePayload(source);
        }

        @Override
        public ActivatePayload[] newArray(int size) {
            return new ActivatePayload[size];
        }
    };
}

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.mifoserror;

import android.os.Parcel;
import android.os.Parcelable;

public class MifosError400 implements Parcelable {

    private String timestamp;
    private String status;
    private String error;
    private String message;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.timestamp);
        dest.writeString(this.status);
        dest.writeString(this.error);
        dest.writeString(this.message);
    }

    public MifosError400() {
    }

    protected MifosError400(Parcel in) {
        this.timestamp = in.readString();
        this.status = in.readString();
        this.error = in.readString();
        this.message = in.readString();
    }

    public static final Creator<MifosError400> CREATOR =
            new Creator<MifosError400>() {
        @Override
        public MifosError400 createFromParcel(Parcel source) {
            return new MifosError400(source);
        }

        @Override
        public MifosError400[] newArray(int size) {
            return new MifosError400[size];
        }
    };
}

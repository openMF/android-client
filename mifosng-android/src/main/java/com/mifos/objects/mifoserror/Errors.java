/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.mifoserror;


import android.os.Parcel;
import android.os.Parcelable;

public class Errors implements Parcelable {

    private String developerMessage;
    private String defaultUserMessage;
    private String userMessageGlobalisationCode;
    private String parameterName;

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public String getDefaultUserMessage() {
        return defaultUserMessage;
    }

    public void setDefaultUserMessage(String defaultUserMessage) {
        this.defaultUserMessage = defaultUserMessage;
    }

    public String getUserMessageGlobalisationCode() {
        return userMessageGlobalisationCode;
    }

    public void setUserMessageGlobalisationCode(String userMessageGlobalisationCode) {
        this.userMessageGlobalisationCode = userMessageGlobalisationCode;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.developerMessage);
        dest.writeString(this.defaultUserMessage);
        dest.writeString(this.userMessageGlobalisationCode);
        dest.writeString(this.parameterName);
    }

    public Errors() {
    }

    protected Errors(Parcel in) {
        this.developerMessage = in.readString();
        this.defaultUserMessage = in.readString();
        this.userMessageGlobalisationCode = in.readString();
        this.parameterName = in.readString();
    }

    public static final Parcelable.Creator<Errors> CREATOR = new Parcelable.Creator<Errors>() {
        @Override
        public Errors createFromParcel(Parcel source) {
            return new Errors(source);
        }

        @Override
        public Errors[] newArray(int size) {
            return new Errors[size];
        }
    };
}


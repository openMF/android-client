/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.mifoserror;


import android.os.Parcel;
import android.os.Parcelable;

public class Arg implements Parcelable {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.value);
    }

    public Arg() {
    }

    protected Arg(Parcel in) {
        this.value = in.readString();
    }

    public static final Parcelable.Creator<Arg> CREATOR = new Parcelable.Creator<Arg>() {
        @Override
        public Arg createFromParcel(Parcel source) {
            return new Arg(source);
        }

        @Override
        public Arg[] newArray(int size) {
            return new Arg[size];
        }
    };
}

package com.mifos.objects.templates.loans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mayankjindal on 02/10/16.
 */

public class OverdueCharges implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public OverdueCharges() {
    }

    protected OverdueCharges(Parcel in) {
    }

    public static final Parcelable.Creator<OverdueCharges> CREATOR =
            new Parcelable.Creator<OverdueCharges>() {
                @Override
                public OverdueCharges createFromParcel(Parcel source) {
                    return new OverdueCharges(source);
                }

                @Override
                public OverdueCharges[] newArray(int size) {
                    return new OverdueCharges[size];
                }
            };
}

package com.mifos.objects.accounts.loan;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nellyk on 3/3/2016.
 */
public class PaymentTypeOptions implements Parcelable {
    int id;
    String name;

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "id" + id + " name:" + name;
    }

    protected PaymentTypeOptions(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PaymentTypeOptions> CREATOR =
            new Parcelable.Creator<PaymentTypeOptions>() {
        @Override
        public PaymentTypeOptions createFromParcel(Parcel in) {
            return new PaymentTypeOptions(in);
        }

        @Override
        public PaymentTypeOptions[] newArray(int size) {
            return new PaymentTypeOptions[size];
        }
    };
}

package com.mifos.objects.templates.clients;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mayankjindal on 13/12/16.
 */

public class ChargeTemplate implements Parcelable {

    @SerializedName("penalty")
    Boolean penalty;

    @SerializedName("chargeOptions")
    List<ChargeOptions> chargeOptions;

    public Boolean getPenalty() {
        return penalty;
    }

    public void setPenalty(Boolean penalty) {
        this.penalty = penalty;
    }

    public List<ChargeOptions> getChargeOptions() {
        return chargeOptions;
    }

    public void setChargeOptions(List<ChargeOptions> chargeOptions) {
        this.chargeOptions = chargeOptions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.penalty);
        dest.writeTypedList(this.chargeOptions);
    }

    public ChargeTemplate() {
    }

    protected ChargeTemplate(Parcel in) {
        this.penalty = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.chargeOptions = in.createTypedArrayList(ChargeOptions.CREATOR);
    }

    public static final Parcelable.Creator<ChargeTemplate> CREATOR = new Parcelable
            .Creator<ChargeTemplate>() {
        @Override
        public ChargeTemplate createFromParcel(Parcel source) {
            return new ChargeTemplate(source);
        }

        @Override
        public ChargeTemplate[] newArray(int size) {
            return new ChargeTemplate[size];
        }
    };
}

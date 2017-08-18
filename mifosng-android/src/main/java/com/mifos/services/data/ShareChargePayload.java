package com.mifos.services.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mayankjindal on 21/08/17.
 */

public class ShareChargePayload implements Parcelable {

    public ShareChargePayload(Integer chargeId, Double amount) {
        this.chargeId = chargeId;
        this.amount = amount;
    }

    @SerializedName("chargeId")
    Integer chargeId;

    @SerializedName("amount")
    Double amount;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getChargeId() {

        return chargeId;
    }

    public void setChargeId(Integer chargeId) {
        this.chargeId = chargeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.chargeId);
        dest.writeValue(this.amount);
    }

    public ShareChargePayload() {
    }

    protected ShareChargePayload(Parcel in) {
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.chargeId = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<ShareChargePayload> CREATOR =
            new Parcelable.Creator<ShareChargePayload>() {
                @Override
                public ShareChargePayload createFromParcel(Parcel source) {
                    return new ShareChargePayload(source);
                }

                @Override
                public ShareChargePayload[] newArray(int size) {
                    return new ShareChargePayload[size];
                }
            };
}

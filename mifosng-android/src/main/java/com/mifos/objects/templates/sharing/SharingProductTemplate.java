package com.mifos.objects.templates.sharing;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mifos.objects.organisation.ProductSharing;
import com.mifos.objects.templates.clients.ChargeOptions;

import java.util.List;

/**
 * Created by mayankjindal on 18/08/17.
 */

public class SharingProductTemplate implements Parcelable {

    @NonNull
    @SerializedName("productOptions")
    List<ProductSharing> productOptions;

    @NonNull
    @SerializedName("chargeOptions")
    List<ChargeOptions> chargeOptions;

    public List<ProductSharing> getProductOptions() {
        return productOptions;
    }

    public void setProductOptions(List<ProductSharing> productOptions) {
        this.productOptions = productOptions;
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
        dest.writeTypedList(this.productOptions);
        dest.writeTypedList(this.chargeOptions);
    }

    public SharingProductTemplate() {
    }

    protected SharingProductTemplate(Parcel in) {
        this.productOptions = in.createTypedArrayList(ProductSharing.CREATOR);
        this.chargeOptions = in.createTypedArrayList(ChargeOptions.CREATOR);
    }

    public static final Parcelable.Creator<SharingProductTemplate> CREATOR =
            new Parcelable.Creator<SharingProductTemplate>() {
                @Override
                public SharingProductTemplate createFromParcel(Parcel source) {
                    return new SharingProductTemplate(source);
                }

                @Override
                public SharingProductTemplate[] newArray(int size) {
                    return new SharingProductTemplate[size];
                }
            };
}

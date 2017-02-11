package com.mifos.objects.templates.loans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


/**
 * Created by mayankjindal on 14/12/16.
 */

public class PaymentTypeOptions implements Parcelable {

    @SerializedName("id")
    Integer id;

    @SerializedName("name")
    String name;

    @SerializedName("description")
    String description;

    @SerializedName("isCashPayment")
    Boolean isCashPayment;

    @SerializedName("position")
    Integer position;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCashPayment() {
        return isCashPayment;
    }

    public void setCashPayment(Boolean cashPayment) {
        isCashPayment = cashPayment;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeValue(this.isCashPayment);
        dest.writeValue(this.position);
    }

    public PaymentTypeOptions() {
    }

    protected PaymentTypeOptions(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.description = in.readString();
        this.isCashPayment = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.position = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<PaymentTypeOptions> CREATOR =
            new Parcelable.Creator<PaymentTypeOptions>() {
                @Override
                public PaymentTypeOptions createFromParcel(Parcel source) {
                    return new PaymentTypeOptions(source);
                }

                @Override
                public PaymentTypeOptions[] newArray(int size) {
                    return new PaymentTypeOptions[size];
                }
            };
}

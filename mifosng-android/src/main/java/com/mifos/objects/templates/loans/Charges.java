package com.mifos.objects.templates.loans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mayankjindal on 02/10/16.
 */

public class Charges implements Parcelable {
    @SerializedName("chargeId")
    Integer chargeId;

    @SerializedName("name")
    String name;

    @SerializedName("chargeTimeType")
    ChargeTimeType chargeTimeType;

    @SerializedName("chargeCalculationType")
    ChargeCalculationType chargeCalculationType;

    @SerializedName("currency")
    Currency currency;

    @SerializedName("amount")
    Double amount;

    @SerializedName("amountPaid")
    Double amountPaid;

    @SerializedName("amountWaived")
    Double amountWaived;

    @SerializedName("amountWrittenOff")
    Double amountWrittenOff;

    @SerializedName("amountOutstanding")
    Double amountOutstanding;

    @SerializedName("amountOrPercentage")
    Double amountOrPercentage;

    @SerializedName("penalty")
    Boolean penalty;

    @SerializedName("chargePaymentMode")
    ChargePaymentMode chargePaymentMode;

    @SerializedName("paid")
    Boolean paid;

    @SerializedName("waived")
    Boolean waived;

    @SerializedName("chargePayable")
    Boolean chargePayable;

    public Integer getChargeId() {
        return chargeId;
    }

    public void setChargeId(Integer chargeId) {
        this.chargeId = chargeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChargeTimeType getChargeTimeType() {
        return chargeTimeType;
    }

    public void setChargeTimeType(ChargeTimeType chargeTimeType) {
        this.chargeTimeType = chargeTimeType;
    }

    public ChargeCalculationType getChargeCalculationType() {
        return chargeCalculationType;
    }

    public void setChargeCalculationType(ChargeCalculationType chargeCalculationType) {
        this.chargeCalculationType = chargeCalculationType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Double getAmountWaived() {
        return amountWaived;
    }

    public void setAmountWaived(Double amountWaived) {
        this.amountWaived = amountWaived;
    }

    public Double getAmountWrittenOff() {
        return amountWrittenOff;
    }

    public void setAmountWrittenOff(Double amountWrittenOff) {
        this.amountWrittenOff = amountWrittenOff;
    }

    public Double getAmountOutstanding() {
        return amountOutstanding;
    }

    public void setAmountOutstanding(Double amountOutstanding) {
        this.amountOutstanding = amountOutstanding;
    }

    public Double getAmountOrPercentage() {
        return amountOrPercentage;
    }

    public void setAmountOrPercentage(Double amountOrPercentage) {
        this.amountOrPercentage = amountOrPercentage;
    }

    public Boolean getPenalty() {
        return penalty;
    }

    public void setPenalty(Boolean penalty) {
        this.penalty = penalty;
    }

    public ChargePaymentMode getChargePaymentMode() {
        return chargePaymentMode;
    }

    public void setChargePaymentMode(ChargePaymentMode chargePaymentMode) {
        this.chargePaymentMode = chargePaymentMode;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Boolean getWaived() {
        return waived;
    }

    public void setWaived(Boolean waived) {
        this.waived = waived;
    }

    public Boolean getChargePayable() {
        return chargePayable;
    }

    public void setChargePayable(Boolean chargePayable) {
        this.chargePayable = chargePayable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.chargeId);
        dest.writeString(this.name);
        dest.writeParcelable(this.chargeTimeType, flags);
        dest.writeParcelable(this.chargeCalculationType, flags);
        dest.writeParcelable(this.currency, flags);
        dest.writeValue(this.amount);
        dest.writeValue(this.amountPaid);
        dest.writeValue(this.amountWaived);
        dest.writeValue(this.amountWrittenOff);
        dest.writeValue(this.amountOutstanding);
        dest.writeValue(this.amountOrPercentage);
        dest.writeValue(this.penalty);
        dest.writeParcelable(this.chargePaymentMode, flags);
        dest.writeValue(this.paid);
        dest.writeValue(this.waived);
        dest.writeValue(this.chargePayable);
    }

    public Charges() {
    }

    protected Charges(Parcel in) {
        this.chargeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.chargeTimeType = in.readParcelable(ChargeTimeType.class.getClassLoader());
        this.chargeCalculationType = in.readParcelable(ChargeTimeType.class.getClassLoader());
        this.currency = in.readParcelable(ChargeTimeType.class.getClassLoader());
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.amountPaid = (Double) in.readValue(Double.class.getClassLoader());
        this.amountWaived = (Double) in.readValue(Double.class.getClassLoader());
        this.amountWrittenOff = (Double) in.readValue(Double.class.getClassLoader());
        this.amountOutstanding = (Double) in.readValue(Double.class.getClassLoader());
        this.amountOrPercentage = (Double) in.readValue(Double.class.getClassLoader());
        this.penalty = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.chargePaymentMode = in.readParcelable(ChargeTimeType.class.getClassLoader());
        this.paid = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.waived = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.chargePayable = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Charges> CREATOR =
            new Parcelable.Creator<Charges>() {
                @Override
                public Charges createFromParcel(Parcel source) {
                    return new Charges(source);
                }

                @Override
                public Charges[] newArray(int size) {
                    return new Charges[size];
                }
            };
}

package com.mifos.services.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mayankjindal on 21/08/17.
 */

public class SharingPayload implements Parcelable {

    @NonNull
    @SerializedName("productId")
    Integer productId;

    @NonNull
    @SerializedName("clientId")
    Integer clientId;

    @NonNull
    @SerializedName("requestedShares")
    Integer requestedShares;

    @SerializedName("externalId")
    String externalId;

    @SerializedName("submittedDate")
    String submittedDate;

    @SerializedName("minimumActivePeriod")
    String minimumActivePeriod;

    @NonNull
    @SerializedName("minimumActivePeriodFrequencyType")
    Integer minimumActivePeriodFrequencyType;

    @SerializedName("lockinPeriodFrequency")
    String lockinPeriodFrequency;

    @NonNull
    @SerializedName("lockinPeriodFrequencyType")
    Integer lockinPeriodFrequencyType;

    @SerializedName("applicationDate")
    String applicationDate;

    @SerializedName("allowDividendCalculationForInactiveClients")
    Boolean allowDividendCalculationForInactiveClients;

    @SerializedName("locale")
    String locale;

    @SerializedName("dateFormat")
    String dateFormat;

    @NonNull
    @SerializedName("charges")
    List<ShareChargePayload> charges;

    @NonNull
    @SerializedName("savingsAccountId")
    Integer savingsAccountId;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public List<ShareChargePayload> getChargePayload() {
        return charges;
    }

    public void setChargePayload(List<ShareChargePayload> chargePayload) {
        this.charges = chargePayload;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getRequestedShares() {
        return requestedShares;
    }

    public void setRequestedShares(Integer requestedShares) {
        this.requestedShares = requestedShares;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getMinimumActivePeriod() {
        return minimumActivePeriod;
    }

    public void setMinimumActivePeriod(String minimumActivePeriod) {
        this.minimumActivePeriod = minimumActivePeriod;
    }

    public Integer getMinimumActivePeriodFrequencyType() {
        return minimumActivePeriodFrequencyType;
    }

    public void setMinimumActivePeriodFrequencyType(Integer minimumActivePeriodFrequencyType) {
        this.minimumActivePeriodFrequencyType = minimumActivePeriodFrequencyType;
    }

    public String getLockinPeriodFrequency() {
        return lockinPeriodFrequency;
    }

    public void setLockinPeriodFrequency(String lockinPeriodFrequency) {
        this.lockinPeriodFrequency = lockinPeriodFrequency;
    }

    public Integer getLockinPeriodFrequencyType() {
        return lockinPeriodFrequencyType;
    }

    public void setLockinPeriodFrequencyType(Integer lockinPeriodFrequencyType) {
        this.lockinPeriodFrequencyType = lockinPeriodFrequencyType;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Boolean getAllowDividendCalculationForInactiveClients() {
        return allowDividendCalculationForInactiveClients;
    }

    public void setAllowDividendCalculationForInactiveClients(
            Boolean allowDividendCalculationForInactiveClients) {
        this.allowDividendCalculationForInactiveClients =
                allowDividendCalculationForInactiveClients;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Integer getSavingsAccountId() {
        return savingsAccountId;
    }

    public void setSavingsAccountId(Integer savingsAccountId) {
        this.savingsAccountId = savingsAccountId;
    }

    @Override
    public String toString() {
        return "SharingPayload{" +
                "productId=" + productId +
                ", clientId='" + clientId + '\'' +
                ", requestedShares='" + requestedShares + '\'' +
                ", externalId='" + externalId + '\'' +
                ", submittedDate='" + submittedDate + '\'' +
                ", minimumActivePeriod=" + minimumActivePeriod +
                ", minimumActivePeriodFrequencyType=" + minimumActivePeriodFrequencyType +
                ", lockinPeriodFrequency=" + lockinPeriodFrequency +
                ", lockinPeriodFrequencyType=" + lockinPeriodFrequencyType +
                ", applicationDate='" + applicationDate + '\'' +
                ", allowDividendCalculationForInactiveClients='" +
                allowDividendCalculationForInactiveClients + '\'' +
                ", locale='" + locale + '\'' +
                ", dateFormat='" + dateFormat + '\'' +
                ", externalId='" + externalId + '\'' +
                ", chargePayload=" + charges +
                ", savingsAccountId=" + savingsAccountId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.productId);
        dest.writeValue(this.clientId);
        dest.writeValue(this.requestedShares);
        dest.writeString(this.externalId);
        dest.writeString(this.submittedDate);
        dest.writeString(this.minimumActivePeriod);
        dest.writeValue(this.minimumActivePeriodFrequencyType);
        dest.writeString(this.lockinPeriodFrequency);
        dest.writeValue(this.lockinPeriodFrequencyType);
        dest.writeString(this.applicationDate);
        dest.writeValue(this.allowDividendCalculationForInactiveClients);
        dest.writeString(this.locale);
        dest.writeString(this.dateFormat);
        dest.writeTypedList(this.charges);
        dest.writeValue(this.savingsAccountId);
    }

    public SharingPayload() {
    }

    protected SharingPayload(Parcel in) {
        this.productId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.clientId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.requestedShares = (Integer) in.readValue(Integer.class.getClassLoader());
        this.externalId = in.readString();
        this.submittedDate = in.readString();
        this.minimumActivePeriod = in.readString();
        this.minimumActivePeriodFrequencyType = (Integer)
                in.readValue(Integer.class.getClassLoader());
        this.lockinPeriodFrequency = in.readString();
        this.lockinPeriodFrequencyType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.applicationDate = in.readString();
        this.allowDividendCalculationForInactiveClients = (Boolean)
                in.readValue(Boolean.class.getClassLoader());
        this.locale = in.readString();
        this.dateFormat = in.readString();
        this.charges = in.createTypedArrayList(ShareChargePayload.CREATOR);
        this.savingsAccountId = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<SharingPayload> CREATOR =
            new Parcelable.Creator<SharingPayload>() {
                @Override
                public SharingPayload createFromParcel(Parcel source) {
                    return new SharingPayload(source);
                }

                @Override
                public SharingPayload[] newArray(int size) {
                    return new SharingPayload[size];
                }
            };
}

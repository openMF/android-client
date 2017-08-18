package com.mifos.objects.templates.sharing;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mifos.objects.accounts.savings.Charge;
import com.mifos.objects.accounts.savings.Currency;
import com.mifos.objects.accounts.savings.SavingsAccount;
import com.mifos.objects.common.InterestType;

import java.util.List;

/**
 * Created by mayankjindal on 18/08/17.
 */

public class SharingAccountsTemplate implements Parcelable {

    @NonNull
    @SerializedName("currency")
    Currency currency;

    @NonNull
    @SerializedName("chargesOptions")
    List<Charge> chargesOptions;

    @NonNull
    @SerializedName("lockinPeriodFrequencyTypeOptions")
    List<InterestType> lockinPeriodFrequencyTypeOptions;

    @NonNull
    @SerializedName("minimumActivePeriodFrequencyTypeOptions")
    List<InterestType> minimumActivePeriodFrequencyTypeOptions;

    @NonNull
    @SerializedName("clientSavingsAccounts")
    List<SavingsAccount> clientSavingsAccounts;

    @SerializedName("defaultShares")
    int defaultShares;

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public List<Charge> getChargesOptions() {
        return chargesOptions;
    }

    public void setChargesOptions(List<Charge> chargesOptions) {
        this.chargesOptions = chargesOptions;
    }

    public List<InterestType> getLockinPeriodFrequencyTypeOptions() {
        return lockinPeriodFrequencyTypeOptions;
    }

    public void setLockinPeriodFrequencyTypeOptions(
            List<InterestType> lockinPeriodFrequencyTypeOptions) {
        this.lockinPeriodFrequencyTypeOptions = lockinPeriodFrequencyTypeOptions;
    }

    public List<InterestType> getMinimumActivePeriodFrequencyTypeOptions() {
        return minimumActivePeriodFrequencyTypeOptions;
    }

    public void setMinimumActivePeriodFrequencyTypeOptions(
            List<InterestType> minimumActivePeriodFrequencyTypeOptions) {
        this.minimumActivePeriodFrequencyTypeOptions = minimumActivePeriodFrequencyTypeOptions;
    }

    public List<SavingsAccount> getClientSavingsAccounts() {
        return clientSavingsAccounts;
    }

    public void setClientSavingsAccounts(List<SavingsAccount> clientSavingsAccounts) {
        this.clientSavingsAccounts = clientSavingsAccounts;
    }

    public int getDefaultShares() {
        return defaultShares;
    }

    public void setDefaultShares(int defaultShares) {
        this.defaultShares = defaultShares;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.currency, flags);
        dest.writeTypedList(this.lockinPeriodFrequencyTypeOptions);
        dest.writeTypedList(this.lockinPeriodFrequencyTypeOptions);
        dest.writeTypedList(this.minimumActivePeriodFrequencyTypeOptions);
        dest.writeTypedList(this.clientSavingsAccounts);
        dest.writeValue(this.defaultShares);
    }

    public SharingAccountsTemplate() {
    }

    protected SharingAccountsTemplate(Parcel in) {
        this.defaultShares = (Integer) in.readValue(Integer.class.getClassLoader());
        this.lockinPeriodFrequencyTypeOptions = in.createTypedArrayList(InterestType.CREATOR);
        this.lockinPeriodFrequencyTypeOptions = in.createTypedArrayList(InterestType.CREATOR);
        this.minimumActivePeriodFrequencyTypeOptions = in.
                createTypedArrayList(InterestType.CREATOR);
        this.clientSavingsAccounts = in.createTypedArrayList(SavingsAccount.CREATOR);
    }

    public static final Parcelable.Creator<SharingAccountsTemplate> CREATOR =
            new Parcelable.Creator<SharingAccountsTemplate>() {
                @Override
                public SharingAccountsTemplate createFromParcel(Parcel source) {
                    return new SharingAccountsTemplate(source);
                }

                @Override
                public SharingAccountsTemplate[] newArray(int size) {
                    return new SharingAccountsTemplate[size];
                }
            };
}

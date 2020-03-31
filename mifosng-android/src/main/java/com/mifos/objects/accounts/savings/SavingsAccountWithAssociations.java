/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.savings;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

@Table(database = MifosDatabase.class)
@ModelContainer
public class SavingsAccountWithAssociations extends MifosBaseModel implements Parcelable {

    @SerializedName("id")
    @PrimaryKey
    Integer id;

    @SerializedName("accountNo")
    @Column
    String accountNo;

    @SerializedName("clientId")
    Integer clientId;

    @SerializedName("clientName")
    @Column
    String clientName;

    @SerializedName("savingsProductId")
    Integer savingsProductId;

    @SerializedName("savingsProductName")
    @Column
    String savingsProductName;

    @SerializedName("fieldOfficerId")
    Integer fieldOfficerId;

    @SerializedName("status")
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Status status;

    @SerializedName("timeline")
    Timeline timeline;

    @SerializedName("currency")
    Currency currency;

    @SerializedName("nominalAnnualInterestRate")
    Double nominalAnnualInterestRate;

    @SerializedName("interestCompoundingPeriodType")
    InterestCompoundingPeriodType interestCompoundingPeriodType;

    @SerializedName("interestPostingPeriodType")
    InterestPostingPeriodType interestPostingPeriodType;

    @SerializedName("interestCalculationType")
    InterestCalculationType interestCalculationType;

    @SerializedName("interestCalculationDaysInYearType")
    InterestCalculationDaysInYearType interestCalculationDaysInYearType;

    @SerializedName("minRequiredOpeningBalance")
    Double minRequiredOpeningBalance;

    @SerializedName("lockinPeriodFrequency")
    Integer lockinPeriodFrequency;

    @SerializedName("lockinPeriodFrequencyType")
    LockinPeriodFrequencyType lockinPeriodFrequencyType;

    @SerializedName("withdrawalFeeForTransfers")
    Boolean withdrawalFeeForTransfers;

    @SerializedName("allowOverdraft")
    Boolean allowOverdraft;

    @SerializedName("enforceMinRequiredBalance")
    Boolean enforceMinRequiredBalance;

    @SerializedName("withHoldTax")
    Boolean withHoldTax;

    @SerializedName("lastActiveTransactionDate")
    List<Integer> lastActiveTransactionDate = new ArrayList<>();

    @SerializedName("isDormancyTrackingActive")
    Boolean isDormancyTrackingActive;

    Integer overdraftLimit;

    @SerializedName("summary")
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Summary summary;

    @SerializedName("transactions")
    List<Transaction> transactions = new ArrayList<>();

    @SerializedName("charges")
    List<Charge> charges = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Integer getSavingsProductId() {
        return savingsProductId;
    }

    public void setSavingsProductId(Integer savingsProductId) {
        this.savingsProductId = savingsProductId;
    }

    public String getSavingsProductName() {
        return savingsProductName;
    }

    public void setSavingsProductName(String savingsProductName) {
        this.savingsProductName = savingsProductName;
    }

    public Integer getFieldOfficerId() {
        return fieldOfficerId;
    }

    public void setFieldOfficerId(Integer fieldOfficerId) {
        this.fieldOfficerId = fieldOfficerId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getNominalAnnualInterestRate() {
        return nominalAnnualInterestRate;
    }

    public void setNominalAnnualInterestRate(Double nominalAnnualInterestRate) {
        this.nominalAnnualInterestRate = nominalAnnualInterestRate;
    }

    public InterestCompoundingPeriodType getInterestCompoundingPeriodType() {
        return interestCompoundingPeriodType;
    }

    public void setInterestCompoundingPeriodType(InterestCompoundingPeriodType
                                                         interestCompoundingPeriodType) {
        this.interestCompoundingPeriodType = interestCompoundingPeriodType;
    }

    public InterestPostingPeriodType getInterestPostingPeriodType() {
        return interestPostingPeriodType;
    }

    public void setInterestPostingPeriodType(InterestPostingPeriodType interestPostingPeriodType) {
        this.interestPostingPeriodType = interestPostingPeriodType;
    }

    public InterestCalculationType getInterestCalculationType() {
        return interestCalculationType;
    }

    public void setInterestCalculationType(InterestCalculationType interestCalculationType) {
        this.interestCalculationType = interestCalculationType;
    }

    public InterestCalculationDaysInYearType getInterestCalculationDaysInYearType() {
        return interestCalculationDaysInYearType;
    }

    public void setInterestCalculationDaysInYearType(InterestCalculationDaysInYearType
                                                             interestCalculationDaysInYearType) {
        this.interestCalculationDaysInYearType = interestCalculationDaysInYearType;
    }

    public Double getMinRequiredOpeningBalance() {
        return minRequiredOpeningBalance;
    }

    public void setMinRequiredOpeningBalance(Double minRequiredOpeningBalance) {
        this.minRequiredOpeningBalance = minRequiredOpeningBalance;
    }

    public Integer getLockinPeriodFrequency() {
        return lockinPeriodFrequency;
    }

    public void setLockinPeriodFrequency(Integer lockinPeriodFrequency) {
        this.lockinPeriodFrequency = lockinPeriodFrequency;
    }

    public LockinPeriodFrequencyType getLockinPeriodFrequencyType() {
        return lockinPeriodFrequencyType;
    }

    public void setLockinPeriodFrequencyType(LockinPeriodFrequencyType lockinPeriodFrequencyType) {
        this.lockinPeriodFrequencyType = lockinPeriodFrequencyType;
    }

    public Boolean getWithdrawalFeeForTransfers() {
        return withdrawalFeeForTransfers;
    }

    public void setWithdrawalFeeForTransfers(Boolean withdrawalFeeForTransfers) {
        this.withdrawalFeeForTransfers = withdrawalFeeForTransfers;
    }

    public Boolean getAllowOverdraft() {
        return allowOverdraft;
    }

    public void setAllowOverdraft(Boolean allowOverdraft) {
        this.allowOverdraft = allowOverdraft;
    }

    public Integer getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(Integer overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Charge> getCharges() {
        return charges;
    }

    public void setCharges(List<Charge> charges) {
        this.charges = charges;
    }

    public Boolean getEnforceMinRequiredBalance() {
        return enforceMinRequiredBalance;
    }

    public void setEnforceMinRequiredBalance(Boolean enforceMinRequiredBalance) {
        this.enforceMinRequiredBalance = enforceMinRequiredBalance;
    }

    public Boolean getWithHoldTax() {
        return withHoldTax;
    }

    public void setWithHoldTax(Boolean withHoldTax) {
        this.withHoldTax = withHoldTax;
    }

    public List<Integer> getLastActiveTransactionDate() {
        return lastActiveTransactionDate;
    }

    public void setLastActiveTransactionDate(List<Integer> lastActiveTransactionDate) {
        this.lastActiveTransactionDate = lastActiveTransactionDate;
    }

    public Boolean getDormancyTrackingActive() {
        return isDormancyTrackingActive;
    }

    public void setDormancyTrackingActive(Boolean dormancyTrackingActive) {
        isDormancyTrackingActive = dormancyTrackingActive;
    }

    @Override
    public String toString() {
        return "SavingsAccountWithAssociations{" +
                "id=" + id +
                ", accountNo='" + accountNo + '\'' +
                ", clientId=" + clientId +
                ", clientName='" + clientName + '\'' +
                ", savingsProductId=" + savingsProductId +
                ", savingsProductName='" + savingsProductName + '\'' +
                ", fieldOfficerId=" + fieldOfficerId +
                ", status=" + status +
                ", timeline=" + timeline +
                ", currency=" + currency +
                ", nominalAnnualInterestRate=" + nominalAnnualInterestRate +
                ", interestCompoundingPeriodType=" + interestCompoundingPeriodType +
                ", interestPostingPeriodType=" + interestPostingPeriodType +
                ", interestCalculationType=" + interestCalculationType +
                ", interestCalculationDaysInYearType=" + interestCalculationDaysInYearType +
                ", minRequiredOpeningBalance=" + minRequiredOpeningBalance +
                ", lockinPeriodFrequency=" + lockinPeriodFrequency +
                ", lockinPeriodFrequencyType=" + lockinPeriodFrequencyType +
                ", withdrawalFeeForTransfers=" + withdrawalFeeForTransfers +
                ", allowOverdraft=" + allowOverdraft +
                ", enforceMinRequiredBalance=" + enforceMinRequiredBalance +
                ", withHoldTax=" + withHoldTax +
                ", lastActiveTransactionDate=" + lastActiveTransactionDate +
                ", isDormancyTrackingActive=" + isDormancyTrackingActive +
                ", overdraftLimit=" + overdraftLimit +
                ", summary=" + summary +
                ", transactions=" + transactions +
                ", charges=" + charges +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.accountNo);
        dest.writeValue(this.clientId);
        dest.writeString(this.clientName);
        dest.writeValue(this.savingsProductId);
        dest.writeString(this.savingsProductName);
        dest.writeValue(this.fieldOfficerId);
        dest.writeParcelable(this.status, flags);
        dest.writeParcelable(this.timeline, flags);
        dest.writeParcelable(this.currency, flags);
        dest.writeValue(this.nominalAnnualInterestRate);
        dest.writeParcelable(this.interestCompoundingPeriodType, flags);
        dest.writeParcelable(this.interestPostingPeriodType, flags);
        dest.writeParcelable(this.interestCalculationType, flags);
        dest.writeParcelable(this.interestCalculationDaysInYearType, flags);
        dest.writeValue(this.minRequiredOpeningBalance);
        dest.writeValue(this.lockinPeriodFrequency);
        dest.writeParcelable(this.lockinPeriodFrequencyType, flags);
        dest.writeValue(this.withdrawalFeeForTransfers);
        dest.writeValue(this.allowOverdraft);
        dest.writeValue(this.enforceMinRequiredBalance);
        dest.writeValue(this.withHoldTax);
        dest.writeList(this.lastActiveTransactionDate);
        dest.writeValue(this.isDormancyTrackingActive);
        dest.writeValue(this.overdraftLimit);
        dest.writeParcelable(this.summary, flags);
        dest.writeList(this.transactions);
        dest.writeList(this.charges);
    }

    public SavingsAccountWithAssociations() {
    }

    protected SavingsAccountWithAssociations(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.accountNo = in.readString();
        this.clientId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.clientName = in.readString();
        this.savingsProductId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.savingsProductName = in.readString();
        this.fieldOfficerId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.status = in.readParcelable(Status.class.getClassLoader());
        this.timeline = in.readParcelable(Timeline.class.getClassLoader());
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        this.nominalAnnualInterestRate = (Double) in.readValue(Double.class.getClassLoader());
        this.interestCompoundingPeriodType = in.readParcelable(InterestCompoundingPeriodType
                .class.getClassLoader());
        this.interestPostingPeriodType = in.readParcelable(InterestPostingPeriodType.class
                .getClassLoader());
        this.interestCalculationType = in.readParcelable(InterestCalculationType.class
                .getClassLoader());
        this.interestCalculationDaysInYearType = in.readParcelable
                (InterestCalculationDaysInYearType.class.getClassLoader());
        this.minRequiredOpeningBalance = (Double) in.readValue(Double.class.getClassLoader());
        this.lockinPeriodFrequency = (Integer) in.readValue(Integer.class.getClassLoader());
        this.lockinPeriodFrequencyType = in.readParcelable(LockinPeriodFrequencyType.class
                .getClassLoader());
        this.withdrawalFeeForTransfers = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.allowOverdraft = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.enforceMinRequiredBalance = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.withHoldTax = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.lastActiveTransactionDate = new ArrayList<Integer>();
        in.readList(this.lastActiveTransactionDate, Integer.class.getClassLoader());
        this.isDormancyTrackingActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.overdraftLimit = (Integer) in.readValue(Integer.class.getClassLoader());
        this.summary = in.readParcelable(Summary.class.getClassLoader());
        this.transactions = new ArrayList<Transaction>();
        in.readList(this.transactions, Transaction.class.getClassLoader());
        this.charges = new ArrayList<Charge>();
        in.readList(this.charges, Charge.class.getClassLoader());
    }

    public static final Parcelable.Creator<SavingsAccountWithAssociations> CREATOR =
            new Parcelable.Creator<SavingsAccountWithAssociations>() {
        @Override
        public SavingsAccountWithAssociations createFromParcel(Parcel source) {
            return new SavingsAccountWithAssociations(source);
        }

        @Override
        public SavingsAccountWithAssociations[] newArray(int size) {
            return new SavingsAccountWithAssociations[size];
        }
    };
}

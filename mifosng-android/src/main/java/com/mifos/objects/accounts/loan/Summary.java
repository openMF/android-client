/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.loan;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * This Model is saving the Summary of the Loans according to their Id's
 *
 */
@Table(database = MifosDatabase.class, name = "LoansAccountSummary")
@ModelContainer
public class Summary extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    transient Integer loanId;

    @SerializedName("Currency")
    Currency currency;

    @Column
    @SerializedName("principalDisbursed")
    Integer principalDisbursed;

    @Column
    @SerializedName("principalPaid")
    Double principalPaid;

    @SerializedName("principalWrittenOff")
    Integer principalWrittenOff;

    @Column
    @SerializedName("principalOutstanding")
    Double principalOutstanding;

    @SerializedName("principalOverdue")
    Double principalOverdue;

    @Column
    @SerializedName("interestCharged")
    Double interestCharged;

    @Column
    @SerializedName("interestPaid")
    Double interestPaid;

    @SerializedName("interestWaived")
    Double interestWaived;

    @SerializedName("interestWrittenOff")
    Integer interestWrittenOff;

    @Column
    @SerializedName("interestOutstanding")
    Double interestOutstanding;

    @SerializedName("interestOverdue")
    Double interestOverdue;

    @Column
    @SerializedName("feeChargesCharged")
    Double feeChargesCharged;

    @SerializedName("feeChargesDueAtDisbursementCharged")
    Integer feeChargesDueAtDisbursementCharged;

    @Column
    @SerializedName("feeChargesPaid")
    Double feeChargesPaid;

    @SerializedName("feeChargesWaived")
    Double feeChargesWaived;

    @SerializedName("feeChargesWrittenOff")
    Integer feeChargesWrittenOff;

    @Column
    @SerializedName("feeChargesOutstanding")
    Integer feeChargesOutstanding;

    @SerializedName("feeChargesOverdue")
    Double feeChargesOverdue;

    @Column
    @SerializedName("penaltyChargesCharged")
    Integer penaltyChargesCharged;

    @Column
    @SerializedName("penaltyChargesPaid")
    Integer penaltyChargesPaid;

    @SerializedName("penaltyChargesWaived")
    Integer penaltyChargesWaived;

    @SerializedName("penaltyChargesWrittenOff")
    Integer penaltyChargesWrittenOff;

    @Column
    @SerializedName("penaltyChargesOutstanding")
    Integer penaltyChargesOutstanding;

    @SerializedName("penaltyChargesOverdue")
    Integer penaltyChargesOverdue;

    @Column
    @SerializedName("totalExpectedRepayment")
    Double totalExpectedRepayment;

    @Column
    @SerializedName("totalRepayment")
    Double totalRepayment;

    @SerializedName("totalExpectedCostOfLoan")
    Double totalExpectedCostOfLoan;

    @SerializedName("totalCostOfLoan")
    Double totalCostOfLoan;

    @SerializedName("totalWaived")
    Double totalWaived;

    @SerializedName("totalWrittenOff")
    Integer totalWrittenOff;

    @Column
    @SerializedName("totalOutstanding")
    Double totalOutstanding;

    @Column
    @SerializedName("totalOverdue")
    Double totalOverdue;

    @SerializedName("overdueSinceDate")
    List<Integer> overdueSinceDate;

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Integer getPrincipalDisbursed() {
        return principalDisbursed;
    }

    public void setPrincipalDisbursed(Integer principalDisbursed) {
        this.principalDisbursed = principalDisbursed;
    }

    public Double getPrincipalPaid() {
        return principalPaid;
    }

    public void setPrincipalPaid(Double principalPaid) {
        this.principalPaid = principalPaid;
    }

    public Integer getPrincipalWrittenOff() {
        return principalWrittenOff;
    }

    public void setPrincipalWrittenOff(Integer principalWrittenOff) {
        this.principalWrittenOff = principalWrittenOff;
    }

    public Double getPrincipalOutstanding() {
        return principalOutstanding;
    }

    public void setPrincipalOutstanding(Double principalOutstanding) {
        this.principalOutstanding = principalOutstanding;
    }

    public Double getPrincipalOverdue() {
        return principalOverdue;
    }

    public void setPrincipalOverdue(Double principalOverdue) {
        this.principalOverdue = principalOverdue;
    }

    public Double getInterestCharged() {
        return interestCharged;
    }

    public void setInterestCharged(Double interestCharged) {
        this.interestCharged = interestCharged;
    }

    public Double getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(Double interestPaid) {
        this.interestPaid = interestPaid;
    }

    public Double getInterestWaived() {
        return interestWaived;
    }

    public void setInterestWaived(Double interestWaived) {
        this.interestWaived = interestWaived;
    }

    public Integer getInterestWrittenOff() {
        return interestWrittenOff;
    }

    public void setInterestWrittenOff(Integer interestWrittenOff) {
        this.interestWrittenOff = interestWrittenOff;
    }

    public Double getInterestOutstanding() {
        return interestOutstanding;
    }

    public void setInterestOutstanding(Double interestOutstanding) {
        this.interestOutstanding = interestOutstanding;
    }

    public Double getInterestOverdue() {
        return interestOverdue;
    }

    public void setInterestOverdue(Double interestOverdue) {
        this.interestOverdue = interestOverdue;
    }

    public Double getFeeChargesCharged() {
        return feeChargesCharged;
    }

    public void setFeeChargesCharged(Double feeChargesCharged) {
        this.feeChargesCharged = feeChargesCharged;
    }

    public Integer getFeeChargesDueAtDisbursementCharged() {
        return feeChargesDueAtDisbursementCharged;
    }

    public void setFeeChargesDueAtDisbursementCharged(Integer feeChargesDueAtDisbursementCharged) {
        this.feeChargesDueAtDisbursementCharged = feeChargesDueAtDisbursementCharged;
    }

    public Double getFeeChargesPaid() {
        return feeChargesPaid;
    }

    public void setFeeChargesPaid(Double feeChargesPaid) {
        this.feeChargesPaid = feeChargesPaid;
    }

    public Double getFeeChargesWaived() {
        return feeChargesWaived;
    }

    public void setFeeChargesWaived(Double feeChargesWaived) {
        this.feeChargesWaived = feeChargesWaived;
    }

    public Integer getFeeChargesWrittenOff() {
        return feeChargesWrittenOff;
    }

    public void setFeeChargesWrittenOff(Integer feeChargesWrittenOff) {
        this.feeChargesWrittenOff = feeChargesWrittenOff;
    }

    public Integer getFeeChargesOutstanding() {
        return feeChargesOutstanding;
    }

    public void setFeeChargesOutstanding(Integer feeChargesOutstanding) {
        this.feeChargesOutstanding = feeChargesOutstanding;
    }

    public Double getFeeChargesOverdue() {
        return feeChargesOverdue;
    }

    public void setFeeChargesOverdue(Double feeChargesOverdue) {
        this.feeChargesOverdue = feeChargesOverdue;
    }

    public Integer getPenaltyChargesCharged() {
        return penaltyChargesCharged;
    }

    public void setPenaltyChargesCharged(Integer penaltyChargesCharged) {
        this.penaltyChargesCharged = penaltyChargesCharged;
    }

    public Integer getPenaltyChargesPaid() {
        return penaltyChargesPaid;
    }

    public void setPenaltyChargesPaid(Integer penaltyChargesPaid) {
        this.penaltyChargesPaid = penaltyChargesPaid;
    }

    public Integer getPenaltyChargesWaived() {
        return penaltyChargesWaived;
    }

    public void setPenaltyChargesWaived(Integer penaltyChargesWaived) {
        this.penaltyChargesWaived = penaltyChargesWaived;
    }

    public Integer getPenaltyChargesWrittenOff() {
        return penaltyChargesWrittenOff;
    }

    public void setPenaltyChargesWrittenOff(Integer penaltyChargesWrittenOff) {
        this.penaltyChargesWrittenOff = penaltyChargesWrittenOff;
    }

    public Integer getPenaltyChargesOutstanding() {
        return penaltyChargesOutstanding;
    }

    public void setPenaltyChargesOutstanding(Integer penaltyChargesOutstanding) {
        this.penaltyChargesOutstanding = penaltyChargesOutstanding;
    }

    public Integer getPenaltyChargesOverdue() {
        return penaltyChargesOverdue;
    }

    public void setPenaltyChargesOverdue(Integer penaltyChargesOverdue) {
        this.penaltyChargesOverdue = penaltyChargesOverdue;
    }

    public Double getTotalExpectedRepayment() {
        return totalExpectedRepayment;
    }

    public void setTotalExpectedRepayment(Double totalExpectedRepayment) {
        this.totalExpectedRepayment = totalExpectedRepayment;
    }

    public Double getTotalRepayment() {
        return totalRepayment;
    }

    public void setTotalRepayment(Double totalRepayment) {
        this.totalRepayment = totalRepayment;
    }

    public Double getTotalExpectedCostOfLoan() {
        return totalExpectedCostOfLoan;
    }

    public void setTotalExpectedCostOfLoan(Double totalExpectedCostOfLoan) {
        this.totalExpectedCostOfLoan = totalExpectedCostOfLoan;
    }

    public Double getTotalCostOfLoan() {
        return totalCostOfLoan;
    }

    public void setTotalCostOfLoan(Double totalCostOfLoan) {
        this.totalCostOfLoan = totalCostOfLoan;
    }

    public Double getTotalWaived() {
        return totalWaived;
    }

    public void setTotalWaived(Double totalWaived) {
        this.totalWaived = totalWaived;
    }

    public Integer getTotalWrittenOff() {
        return totalWrittenOff;
    }

    public void setTotalWrittenOff(Integer totalWrittenOff) {
        this.totalWrittenOff = totalWrittenOff;
    }

    public Double getTotalOutstanding() {
        return totalOutstanding;
    }

    public void setTotalOutstanding(Double totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }

    public Double getTotalOverdue() {
        return totalOverdue;
    }

    public void setTotalOverdue(Double totalOverdue) {
        this.totalOverdue = totalOverdue;
    }

    public List<Integer> getOverdueSinceDate() {
        return overdueSinceDate;
    }

    public void setOverdueSinceDate(List<Integer> overdueSinceDate) {
        this.overdueSinceDate = overdueSinceDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.currency, flags);
        dest.writeValue(this.principalDisbursed);
        dest.writeValue(this.principalPaid);
        dest.writeValue(this.principalWrittenOff);
        dest.writeValue(this.principalOutstanding);
        dest.writeValue(this.principalOverdue);
        dest.writeValue(this.interestCharged);
        dest.writeValue(this.interestPaid);
        dest.writeValue(this.interestWaived);
        dest.writeValue(this.interestWrittenOff);
        dest.writeValue(this.interestOutstanding);
        dest.writeValue(this.interestOverdue);
        dest.writeValue(this.feeChargesCharged);
        dest.writeValue(this.feeChargesDueAtDisbursementCharged);
        dest.writeValue(this.feeChargesPaid);
        dest.writeValue(this.feeChargesWaived);
        dest.writeValue(this.feeChargesWrittenOff);
        dest.writeValue(this.feeChargesOutstanding);
        dest.writeValue(this.feeChargesOverdue);
        dest.writeValue(this.penaltyChargesCharged);
        dest.writeValue(this.penaltyChargesPaid);
        dest.writeValue(this.penaltyChargesWaived);
        dest.writeValue(this.penaltyChargesWrittenOff);
        dest.writeValue(this.penaltyChargesOutstanding);
        dest.writeValue(this.penaltyChargesOverdue);
        dest.writeValue(this.totalExpectedRepayment);
        dest.writeValue(this.totalRepayment);
        dest.writeValue(this.totalExpectedCostOfLoan);
        dest.writeValue(this.totalCostOfLoan);
        dest.writeValue(this.totalWaived);
        dest.writeValue(this.totalWrittenOff);
        dest.writeValue(this.totalOutstanding);
        dest.writeValue(this.totalOverdue);
        dest.writeList(this.overdueSinceDate);
    }

    public Summary() {
    }

    protected Summary(Parcel in) {
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        this.principalDisbursed = (Integer) in.readValue(Integer.class.getClassLoader());
        this.principalPaid = (Double) in.readValue(Double.class.getClassLoader());
        this.principalWrittenOff = (Integer) in.readValue(Integer.class.getClassLoader());
        this.principalOutstanding = (Double) in.readValue(Double.class.getClassLoader());
        this.principalOverdue = (Double) in.readValue(Double.class.getClassLoader());
        this.interestCharged = (Double) in.readValue(Double.class.getClassLoader());
        this.interestPaid = (Double) in.readValue(Double.class.getClassLoader());
        this.interestWaived = (Double) in.readValue(Double.class.getClassLoader());
        this.interestWrittenOff = (Integer) in.readValue(Integer.class.getClassLoader());
        this.interestOutstanding = (Double) in.readValue(Double.class.getClassLoader());
        this.interestOverdue = (Double) in.readValue(Double.class.getClassLoader());
        this.feeChargesCharged = (Double) in.readValue(Double.class.getClassLoader());
        this.feeChargesDueAtDisbursementCharged = (Integer) in.readValue(Integer.class
                .getClassLoader());
        this.feeChargesPaid = (Double) in.readValue(Double.class.getClassLoader());
        this.feeChargesWaived = (Double) in.readValue(Double.class.getClassLoader());
        this.feeChargesWrittenOff = (Integer) in.readValue(Integer.class.getClassLoader());
        this.feeChargesOutstanding = (Integer) in.readValue(Integer.class.getClassLoader());
        this.feeChargesOverdue = (Double) in.readValue(Double.class.getClassLoader());
        this.penaltyChargesCharged = (Integer) in.readValue(Integer.class.getClassLoader());
        this.penaltyChargesPaid = (Integer) in.readValue(Integer.class.getClassLoader());
        this.penaltyChargesWaived = (Integer) in.readValue(Integer.class.getClassLoader());
        this.penaltyChargesWrittenOff = (Integer) in.readValue(Integer.class.getClassLoader());
        this.penaltyChargesOutstanding = (Integer) in.readValue(Integer.class.getClassLoader());
        this.penaltyChargesOverdue = (Integer) in.readValue(Integer.class.getClassLoader());
        this.totalExpectedRepayment = (Double) in.readValue(Double.class.getClassLoader());
        this.totalRepayment = (Double) in.readValue(Double.class.getClassLoader());
        this.totalExpectedCostOfLoan = (Double) in.readValue(Double.class.getClassLoader());
        this.totalCostOfLoan = (Double) in.readValue(Double.class.getClassLoader());
        this.totalWaived = (Double) in.readValue(Double.class.getClassLoader());
        this.totalWrittenOff = (Integer) in.readValue(Integer.class.getClassLoader());
        this.totalOutstanding = (Double) in.readValue(Double.class.getClassLoader());
        this.totalOverdue = (Double) in.readValue(Double.class.getClassLoader());
        this.overdueSinceDate = new ArrayList<Integer>();
        in.readList(this.overdueSinceDate, Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Summary> CREATOR = new Parcelable.Creator<Summary>() {
        @Override
        public Summary createFromParcel(Parcel source) {
            return new Summary(source);
        }

        @Override
        public Summary[] newArray(int size) {
            return new Summary[size];
        }
    };
}

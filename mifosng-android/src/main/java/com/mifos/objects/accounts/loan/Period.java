/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.loan;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 19/06/14.
 */
public class Period implements Parcelable {

    Boolean complete;
    Integer daysInPeriod;
    List<Integer> dueDate;
    Double feeChargesDue;
    Double feeChargesOutstanding;
    Double feeChargesPaid;
    Double feeChargesWaived;
    Double feeChargesWrittenOff;
    List<Integer> fromDate;
    Double interestDue;
    Double interestOriginalDue;
    Double interestOutstanding;
    Double interestPaid;
    Double interestWaived;
    Double interestWrittenOff;
    List<Integer> obligationsMetOnDate;
    Double penaltyChargesDue;
    Double penaltyChargesOutstanding;
    Double penaltyChargesPaid;
    Double penaltyChargesWaived;
    Double penaltyChargesWrittenOff;

    @SerializedName("period")
    Integer periodValue;

    Double principalDue;
    Double principalLoanBalanceOutstanding;
    Double principalOriginalDue;
    Double principalOutstanding;
    Double principalPaid;
    Double principalWrittenOff;
    Double totalActualCostOfLoanForPeriod;
    Double totalDueForPeriod;
    Double totalOriginalDueForPeriod;
    Double totalOutstandingForPeriod;
    Double totalOverdue;
    Double totalPaidForPeriod;
    Double totalPaidInAdvanceForPeriod;
    Double totalPaidLateForPeriod;
    Double totalWaivedForPeriod;
    Double totalWrittenOffForPeriod;

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public Integer getDaysInPeriod() {
        return daysInPeriod;
    }

    public void setDaysInPeriod(Integer daysInPeriod) {
        this.daysInPeriod = daysInPeriod;
    }

    public List<Integer> getDueDate() {
        return dueDate;
    }

    public void setDueDate(List<Integer> dueDate) {
        this.dueDate = dueDate;
    }

    public Double getFeeChargesDue() {
        return feeChargesDue;
    }

    public void setFeeChargesDue(Double feeChargesDue) {
        this.feeChargesDue = feeChargesDue;
    }

    public Double getFeeChargesOutstanding() {
        return feeChargesOutstanding;
    }

    public void setFeeChargesOutstanding(Double feeChargesOutstanding) {
        this.feeChargesOutstanding = feeChargesOutstanding;
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

    public Double getFeeChargesWrittenOff() {
        return feeChargesWrittenOff;
    }

    public void setFeeChargesWrittenOff(Double feeChargesWrittenOff) {
        this.feeChargesWrittenOff = feeChargesWrittenOff;
    }

    public List<Integer> getFromDate() {
        return fromDate;
    }

    public void setFromDate(List<Integer> fromDate) {
        this.fromDate = fromDate;
    }

    public Double getInterestDue() {
        return interestDue;
    }

    public void setInterestDue(Double interestDue) {
        this.interestDue = interestDue;
    }

    public Double getInterestOriginalDue() {
        return interestOriginalDue;
    }

    public void setInterestOriginalDue(Double interestOriginalDue) {
        this.interestOriginalDue = interestOriginalDue;
    }

    public Double getInterestOutstanding() {
        return interestOutstanding;
    }

    public void setInterestOutstanding(Double interestOutstanding) {
        this.interestOutstanding = interestOutstanding;
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

    public Double getInterestWrittenOff() {
        return interestWrittenOff;
    }

    public void setInterestWrittenOff(Double interestWrittenOff) {
        this.interestWrittenOff = interestWrittenOff;
    }

    public List<Integer> getObligationsMetOnDate() {
        return obligationsMetOnDate;
    }

    public void setObligationsMetOnDate(List<Integer> obligationsMetOnDate) {
        this.obligationsMetOnDate = obligationsMetOnDate;
    }

    public Double getPenaltyChargesDue() {
        return penaltyChargesDue;
    }

    public void setPenaltyChargesDue(Double penaltyChargesDue) {
        this.penaltyChargesDue = penaltyChargesDue;
    }

    public Double getPenaltyChargesOutstanding() {
        return penaltyChargesOutstanding;
    }

    public void setPenaltyChargesOutstanding(Double penaltyChargesOutstanding) {
        this.penaltyChargesOutstanding = penaltyChargesOutstanding;
    }

    public Double getPenaltyChargesPaid() {
        return penaltyChargesPaid;
    }

    public void setPenaltyChargesPaid(Double penaltyChargesPaid) {
        this.penaltyChargesPaid = penaltyChargesPaid;
    }

    public Double getPenaltyChargesWaived() {
        return penaltyChargesWaived;
    }

    public void setPenaltyChargesWaived(Double penaltyChargesWaived) {
        this.penaltyChargesWaived = penaltyChargesWaived;
    }

    public Double getPenaltyChargesWrittenOff() {
        return penaltyChargesWrittenOff;
    }

    public void setPenaltyChargesWrittenOff(Double penaltyChargesWrittenOff) {
        this.penaltyChargesWrittenOff = penaltyChargesWrittenOff;
    }

    public Integer getPeriod() {
        return periodValue;
    }

    public void setPeriod(Integer period) {
        this.periodValue = period;
    }

    public Double getPrincipalDue() {
        return principalDue;
    }

    public void setPrincipalDue(Double principalDue) {
        this.principalDue = principalDue;
    }

    public Double getPrincipalLoanBalanceOutstanding() {
        return principalLoanBalanceOutstanding;
    }

    public void setPrincipalLoanBalanceOutstanding(Double principalLoanBalanceOutstanding) {
        this.principalLoanBalanceOutstanding = principalLoanBalanceOutstanding;
    }

    public Double getPrincipalOriginalDue() {
        return principalOriginalDue;
    }

    public void setPrincipalOriginalDue(Double principalOriginalDue) {
        this.principalOriginalDue = principalOriginalDue;
    }

    public Double getPrincipalOutstanding() {
        return principalOutstanding;
    }

    public void setPrincipalOutstanding(Double principalOutstanding) {
        this.principalOutstanding = principalOutstanding;
    }

    public Double getPrincipalPaid() {
        return principalPaid;
    }

    public void setPrincipalPaid(Double principalPaid) {
        this.principalPaid = principalPaid;
    }

    public Double getPrincipalWrittenOff() {
        return principalWrittenOff;
    }

    public void setPrincipalWrittenOff(Double principalWrittenOff) {
        this.principalWrittenOff = principalWrittenOff;
    }

    public Double getTotalActualCostOfLoanForPeriod() {
        return totalActualCostOfLoanForPeriod;
    }

    public void setTotalActualCostOfLoanForPeriod(Double totalActualCostOfLoanForPeriod) {
        this.totalActualCostOfLoanForPeriod = totalActualCostOfLoanForPeriod;
    }

    public Double getTotalDueForPeriod() {
        return totalDueForPeriod;
    }

    public void setTotalDueForPeriod(Double totalDueForPeriod) {
        this.totalDueForPeriod = totalDueForPeriod;
    }

    public Double getTotalOriginalDueForPeriod() {
        return totalOriginalDueForPeriod;
    }

    public void setTotalOriginalDueForPeriod(Double totalOriginalDueForPeriod) {
        this.totalOriginalDueForPeriod = totalOriginalDueForPeriod;
    }

    public Double getTotalOutstandingForPeriod() {
        return totalOutstandingForPeriod;
    }

    public void setTotalOutstandingForPeriod(Double totalOutstandingForPeriod) {
        this.totalOutstandingForPeriod = totalOutstandingForPeriod;
    }

    public Double getTotalOverdue() {
        return totalOverdue;
    }

    public void setTotalOverdue(Double totalOverdue) {
        this.totalOverdue = totalOverdue;
    }

    public Double getTotalPaidForPeriod() {
        return totalPaidForPeriod;
    }

    public void setTotalPaidForPeriod(Double totalPaidForPeriod) {
        this.totalPaidForPeriod = totalPaidForPeriod;
    }

    public Double getTotalPaidInAdvanceForPeriod() {
        return totalPaidInAdvanceForPeriod;
    }

    public void setTotalPaidInAdvanceForPeriod(Double totalPaidInAdvanceForPeriod) {
        this.totalPaidInAdvanceForPeriod = totalPaidInAdvanceForPeriod;
    }

    public Double getTotalPaidLateForPeriod() {
        return totalPaidLateForPeriod;
    }

    public void setTotalPaidLateForPeriod(Double totalPaidLateForPeriod) {
        this.totalPaidLateForPeriod = totalPaidLateForPeriod;
    }

    public Double getTotalWaivedForPeriod() {
        return totalWaivedForPeriod;
    }

    public void setTotalWaivedForPeriod(Double totalWaivedForPeriod) {
        this.totalWaivedForPeriod = totalWaivedForPeriod;
    }

    public Double getTotalWrittenOffForPeriod() {
        return totalWrittenOffForPeriod;
    }

    public void setTotalWrittenOffForPeriod(Double totalWrittenOffForPeriod) {
        this.totalWrittenOffForPeriod = totalWrittenOffForPeriod;
    }

    @Override
    public String toString() {
        return "Period{" +
                "complete=" + complete +
                ", daysInPeriod=" + daysInPeriod +
                ", dueDate=" + dueDate +
                ", feeChargesDue=" + feeChargesDue +
                ", feeChargesOutstanding=" + feeChargesOutstanding +
                ", feeChargesPaid=" + feeChargesPaid +
                ", feeChargesWaived=" + feeChargesWaived +
                ", feeChargesWrittenOff=" + feeChargesWrittenOff +
                ", fromDate=" + fromDate +
                ", interestDue=" + interestDue +
                ", interestOriginalDue=" + interestOriginalDue +
                ", interestOutstanding=" + interestOutstanding +
                ", interestPaid=" + interestPaid +
                ", interestWaived=" + interestWaived +
                ", interestWrittenOff=" + interestWrittenOff +
                ", obligationsMetOnDate=" + obligationsMetOnDate +
                ", penaltyChargesDue=" + penaltyChargesDue +
                ", penaltyChargesOutstanding=" + penaltyChargesOutstanding +
                ", penaltyChargesPaid=" + penaltyChargesPaid +
                ", penaltyChargesWaived=" + penaltyChargesWaived +
                ", penaltyChargesWrittenOff=" + penaltyChargesWrittenOff +
                ", period=" + periodValue +
                ", principalDue=" + principalDue +
                ", principalLoanBalanceOutstanding=" + principalLoanBalanceOutstanding +
                ", principalOriginalDue=" + principalOriginalDue +
                ", principalOutstanding=" + principalOutstanding +
                ", principalPaid=" + principalPaid +
                ", principalWrittenOff=" + principalWrittenOff +
                ", totalActualCostOfLoanForPeriod=" + totalActualCostOfLoanForPeriod +
                ", totalDueForPeriod=" + totalDueForPeriod +
                ", totalOriginalDueForPeriod=" + totalOriginalDueForPeriod +
                ", totalOutstandingForPeriod=" + totalOutstandingForPeriod +
                ", totalOverdue=" + totalOverdue +
                ", totalPaidForPeriod=" + totalPaidForPeriod +
                ", totalPaidInAdvanceForPeriod=" + totalPaidInAdvanceForPeriod +
                ", totalPaidLateForPeriod=" + totalPaidLateForPeriod +
                ", totalWaivedForPeriod=" + totalWaivedForPeriod +
                ", totalWrittenOffForPeriod=" + totalWrittenOffForPeriod +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.complete);
        dest.writeValue(this.daysInPeriod);
        dest.writeList(this.dueDate);
        dest.writeValue(this.feeChargesDue);
        dest.writeValue(this.feeChargesOutstanding);
        dest.writeValue(this.feeChargesPaid);
        dest.writeValue(this.feeChargesWaived);
        dest.writeValue(this.feeChargesWrittenOff);
        dest.writeList(this.fromDate);
        dest.writeValue(this.interestDue);
        dest.writeValue(this.interestOriginalDue);
        dest.writeValue(this.interestOutstanding);
        dest.writeValue(this.interestPaid);
        dest.writeValue(this.interestWaived);
        dest.writeValue(this.interestWrittenOff);
        dest.writeList(this.obligationsMetOnDate);
        dest.writeValue(this.penaltyChargesDue);
        dest.writeValue(this.penaltyChargesOutstanding);
        dest.writeValue(this.penaltyChargesPaid);
        dest.writeValue(this.penaltyChargesWaived);
        dest.writeValue(this.penaltyChargesWrittenOff);
        dest.writeValue(this.periodValue);
        dest.writeValue(this.principalDue);
        dest.writeValue(this.principalLoanBalanceOutstanding);
        dest.writeValue(this.principalOriginalDue);
        dest.writeValue(this.principalOutstanding);
        dest.writeValue(this.principalPaid);
        dest.writeValue(this.principalWrittenOff);
        dest.writeValue(this.totalActualCostOfLoanForPeriod);
        dest.writeValue(this.totalDueForPeriod);
        dest.writeValue(this.totalOriginalDueForPeriod);
        dest.writeValue(this.totalOutstandingForPeriod);
        dest.writeValue(this.totalOverdue);
        dest.writeValue(this.totalPaidForPeriod);
        dest.writeValue(this.totalPaidInAdvanceForPeriod);
        dest.writeValue(this.totalPaidLateForPeriod);
        dest.writeValue(this.totalWaivedForPeriod);
        dest.writeValue(this.totalWrittenOffForPeriod);
    }

    public Period() {
    }

    protected Period(Parcel in) {
        this.complete = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.daysInPeriod = (Integer) in.readValue(Integer.class.getClassLoader());
        this.dueDate = new ArrayList<Integer>();
        in.readList(this.dueDate, Integer.class.getClassLoader());
        this.feeChargesDue = (Double) in.readValue(Double.class.getClassLoader());
        this.feeChargesOutstanding = (Double) in.readValue(Double.class.getClassLoader());
        this.feeChargesPaid = (Double) in.readValue(Double.class.getClassLoader());
        this.feeChargesWaived = (Double) in.readValue(Double.class.getClassLoader());
        this.feeChargesWrittenOff = (Double) in.readValue(Double.class.getClassLoader());
        this.fromDate = new ArrayList<Integer>();
        in.readList(this.fromDate, Integer.class.getClassLoader());
        this.interestDue = (Double) in.readValue(Double.class.getClassLoader());
        this.interestOriginalDue = (Double) in.readValue(Double.class.getClassLoader());
        this.interestOutstanding = (Double) in.readValue(Double.class.getClassLoader());
        this.interestPaid = (Double) in.readValue(Double.class.getClassLoader());
        this.interestWaived = (Double) in.readValue(Double.class.getClassLoader());
        this.interestWrittenOff = (Double) in.readValue(Double.class.getClassLoader());
        this.obligationsMetOnDate = new ArrayList<Integer>();
        in.readList(this.obligationsMetOnDate, Integer.class.getClassLoader());
        this.penaltyChargesDue = (Double) in.readValue(Double.class.getClassLoader());
        this.penaltyChargesOutstanding = (Double) in.readValue(Double.class.getClassLoader());
        this.penaltyChargesPaid = (Double) in.readValue(Double.class.getClassLoader());
        this.penaltyChargesWaived = (Double) in.readValue(Double.class.getClassLoader());
        this.penaltyChargesWrittenOff = (Double) in.readValue(Double.class.getClassLoader());
        this.periodValue = (Integer) in.readValue(Integer.class.getClassLoader());
        this.principalDue = (Double) in.readValue(Double.class.getClassLoader());
        this.principalLoanBalanceOutstanding = (Double) in.readValue(Double.class.getClassLoader());
        this.principalOriginalDue = (Double) in.readValue(Double.class.getClassLoader());
        this.principalOutstanding = (Double) in.readValue(Double.class.getClassLoader());
        this.principalPaid = (Double) in.readValue(Double.class.getClassLoader());
        this.principalWrittenOff = (Double) in.readValue(Double.class.getClassLoader());
        this.totalActualCostOfLoanForPeriod = (Double) in.readValue(Double.class.getClassLoader());
        this.totalDueForPeriod = (Double) in.readValue(Double.class.getClassLoader());
        this.totalOriginalDueForPeriod = (Double) in.readValue(Double.class.getClassLoader());
        this.totalOutstandingForPeriod = (Double) in.readValue(Double.class.getClassLoader());
        this.totalOverdue = (Double) in.readValue(Double.class.getClassLoader());
        this.totalPaidForPeriod = (Double) in.readValue(Double.class.getClassLoader());
        this.totalPaidInAdvanceForPeriod = (Double) in.readValue(Double.class.getClassLoader());
        this.totalPaidLateForPeriod = (Double) in.readValue(Double.class.getClassLoader());
        this.totalWaivedForPeriod = (Double) in.readValue(Double.class.getClassLoader());
        this.totalWrittenOffForPeriod = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<Period> CREATOR = new Parcelable.Creator<Period>() {
        @Override
        public Period createFromParcel(Parcel source) {
            return new Period(source);
        }

        @Override
        public Period[] newArray(int size) {
            return new Period[size];
        }
    };
}

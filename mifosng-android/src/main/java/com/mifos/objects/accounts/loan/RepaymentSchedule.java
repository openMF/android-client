/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.loan;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.objects.accounts.savings.Currency;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 19/06/14.
 */
public class RepaymentSchedule implements Parcelable {

    Currency currency;
    Integer loanTermInDays;
    List<Period> periods;
    Double totalFeeChargesCharged;
    Double totalInterestCharged;
    Double totalOutstanding;
    Double totalPaidInAdvance;
    Double totalPaidLate;
    Double totalPenaltyChargesCharged;
    Double totalPrincipalDisbursed;
    Double totalPrincipalExpected;
    Double totalPrincipalPaid;
    Double totalRepayment;
    Double totalRepaymentExpected;
    Double totalWaived;
    Double totalWrittenOff;

    //Helper Method to get total completed repayments
    public static int getNumberOfRepaymentsComplete(List<Period> periodList) {

        int count = 0;

        for (Period period : periodList) {
            if (period.getComplete())
                count++;
        }

        return count;
    }

    //Helper Method to get total pending/upcoming repayments
    public static int getNumberOfRepaymentsPending(List<Period> periodList) {

        int count = 0;

        for (Period period : periodList) {
            if (!period.getComplete())
                count++;

        }

        return count;

    }

    //Helper Method to get total repayments overdue
    public static int getNumberOfRepaymentsOverDue(List<Period> periodList) {

        int count = 0;

        for (Period period : periodList) {
            if (period.getTotalOverdue() != null && period.getTotalOverdue() > 0
                    && !period.getComplete()) {
                count++;
            }

        }

        return count;


    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Integer getLoanTermInDays() {
        return loanTermInDays;
    }

    public void setLoanTermInDays(Integer loanTermInDays) {
        this.loanTermInDays = loanTermInDays;
    }

    public List<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    public Double getTotalFeeChargesCharged() {
        return totalFeeChargesCharged;
    }

    public void setTotalFeeChargesCharged(Double totalFeeChargesCharged) {
        this.totalFeeChargesCharged = totalFeeChargesCharged;
    }

    public Double getTotalInterestCharged() {
        return totalInterestCharged;
    }

    public void setTotalInterestCharged(Double totalInterestCharged) {
        this.totalInterestCharged = totalInterestCharged;
    }

    public Double getTotalOutstanding() {
        return totalOutstanding;
    }

    public void setTotalOutstanding(Double totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }

    public Double getTotalPaidInAdvance() {
        return totalPaidInAdvance;
    }

    public void setTotalPaidInAdvance(Double totalPaidInAdvance) {
        this.totalPaidInAdvance = totalPaidInAdvance;
    }

    public Double getTotalPaidLate() {
        return totalPaidLate;
    }

    public void setTotalPaidLate(Double totalPaidLate) {
        this.totalPaidLate = totalPaidLate;
    }

    public Double getTotalPenaltyChargesCharged() {
        return totalPenaltyChargesCharged;
    }

    public void setTotalPenaltyChargesCharged(Double totalPenaltyChargesCharged) {
        this.totalPenaltyChargesCharged = totalPenaltyChargesCharged;
    }

    public Double getTotalPrincipalDisbursed() {
        return totalPrincipalDisbursed;
    }

    public void setTotalPrincipalDisbursed(Double totalPrincipalDisbursed) {
        this.totalPrincipalDisbursed = totalPrincipalDisbursed;
    }

    public Double getTotalPrincipalExpected() {
        return totalPrincipalExpected;
    }

    public void setTotalPrincipalExpected(Double totalPrincipalExpected) {
        this.totalPrincipalExpected = totalPrincipalExpected;
    }

    public Double getTotalPrincipalPaid() {
        return totalPrincipalPaid;
    }

    public void setTotalPrincipalPaid(Double totalPrincipalPaid) {
        this.totalPrincipalPaid = totalPrincipalPaid;
    }

    public Double getTotalRepayment() {
        return totalRepayment;
    }

    public void setTotalRepayment(Double totalRepayment) {
        this.totalRepayment = totalRepayment;
    }

    public Double getTotalRepaymentExpected() {
        return totalRepaymentExpected;
    }

    public void setTotalRepaymentExpected(Double totalRepaymentExpected) {
        this.totalRepaymentExpected = totalRepaymentExpected;
    }

    public Double getTotalWaived() {
        return totalWaived;
    }

    public void setTotalWaived(Double totalWaived) {
        this.totalWaived = totalWaived;
    }

    public Double getTotalWrittenOff() {
        return totalWrittenOff;
    }

    public void setTotalWrittenOff(Double totalWrittenOff) {
        this.totalWrittenOff = totalWrittenOff;
    }

    public List<Period> getlistOfActualPeriods() {
        return this.periods.subList(1, periods.size());
    }

    @Override
    public String toString() {
        return "RepaymentSchedule{" +
                "currency=" + currency +
                ", loanTermInDays=" + loanTermInDays +
                ", periods=" + periods +
                ", totalFeeChargesCharged=" + totalFeeChargesCharged +
                ", totalInterestCharged=" + totalInterestCharged +
                ", totalOutstanding=" + totalOutstanding +
                ", totalPaidInAdvance=" + totalPaidInAdvance +
                ", totalPaidLate=" + totalPaidLate +
                ", totalPenaltyChargesCharged=" + totalPenaltyChargesCharged +
                ", totalPrincipalDisbursed=" + totalPrincipalDisbursed +
                ", totalPrincipalExpected=" + totalPrincipalExpected +
                ", totalPrincipalPaid=" + totalPrincipalPaid +
                ", totalRepayment=" + totalRepayment +
                ", totalRepaymentExpected=" + totalRepaymentExpected +
                ", totalWaived=" + totalWaived +
                ", totalWrittenOff=" + totalWrittenOff +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.currency, flags);
        dest.writeValue(this.loanTermInDays);
        dest.writeList(this.periods);
        dest.writeValue(this.totalFeeChargesCharged);
        dest.writeValue(this.totalInterestCharged);
        dest.writeValue(this.totalOutstanding);
        dest.writeValue(this.totalPaidInAdvance);
        dest.writeValue(this.totalPaidLate);
        dest.writeValue(this.totalPenaltyChargesCharged);
        dest.writeValue(this.totalPrincipalDisbursed);
        dest.writeValue(this.totalPrincipalExpected);
        dest.writeValue(this.totalPrincipalPaid);
        dest.writeValue(this.totalRepayment);
        dest.writeValue(this.totalRepaymentExpected);
        dest.writeValue(this.totalWaived);
        dest.writeValue(this.totalWrittenOff);
    }

    public RepaymentSchedule() {
    }

    protected RepaymentSchedule(Parcel in) {
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        this.loanTermInDays = (Integer) in.readValue(Integer.class.getClassLoader());
        this.periods = new ArrayList<Period>();
        in.readList(this.periods, Period.class.getClassLoader());
        this.totalFeeChargesCharged = (Double) in.readValue(Double.class.getClassLoader());
        this.totalInterestCharged = (Double) in.readValue(Double.class.getClassLoader());
        this.totalOutstanding = (Double) in.readValue(Double.class.getClassLoader());
        this.totalPaidInAdvance = (Double) in.readValue(Double.class.getClassLoader());
        this.totalPaidLate = (Double) in.readValue(Double.class.getClassLoader());
        this.totalPenaltyChargesCharged = (Double) in.readValue(Double.class.getClassLoader());
        this.totalPrincipalDisbursed = (Double) in.readValue(Double.class.getClassLoader());
        this.totalPrincipalExpected = (Double) in.readValue(Double.class.getClassLoader());
        this.totalPrincipalPaid = (Double) in.readValue(Double.class.getClassLoader());
        this.totalRepayment = (Double) in.readValue(Double.class.getClassLoader());
        this.totalRepaymentExpected = (Double) in.readValue(Double.class.getClassLoader());
        this.totalWaived = (Double) in.readValue(Double.class.getClassLoader());
        this.totalWrittenOff = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<RepaymentSchedule> CREATOR =
            new Parcelable.Creator<RepaymentSchedule>() {
        @Override
        public RepaymentSchedule createFromParcel(Parcel source) {
            return new RepaymentSchedule(source);
        }

        @Override
        public RepaymentSchedule[] newArray(int size) {
            return new RepaymentSchedule[size];
        }
    };
}

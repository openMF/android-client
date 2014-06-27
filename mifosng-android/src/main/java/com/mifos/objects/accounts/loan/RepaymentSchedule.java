package com.mifos.objects.accounts.loan;

import com.mifos.objects.Currency;

import java.util.List;

/**
 * Created by ishankhanna on 19/06/14.
 */
public class RepaymentSchedule {

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
        return this.periods.subList(1,periods.size());
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

    //Helper Method to get total completed repayments
    public static int getNumberOfRepaymentsComplete(List<Period> periodList) {

        int count = 0;

        for(Period period : periodList) {
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
            if (period.getTotalOverdue()!=null && period.getTotalOverdue()>0) {
                if (!period.getComplete())
                    count++;
            }

        }

        return count;


    }

}


package com.mifos.objects.accounts.loan;

import com.mifos.objects.Currency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Summary {

    private Currency currency;
    private Integer principalDisbursed;
    private Double principalPaid;
    private Integer principalWrittenOff;
    private Double principalOutstanding;
    private Double principalOverdue;
    private Double interestCharged;
    private Double interestPaid;
    private Integer interestWaived;
    private Integer interestWrittenOff;
    private Double interestOutstanding;
    private Double interestOverdue;
    private Integer feeChargesCharged;
    private Integer feeChargesDueAtDisbursementCharged;
    private Integer feeChargesPaid;
    private Integer feeChargesWaived;
    private Integer feeChargesWrittenOff;
    private Integer feeChargesOutstanding;
    private Integer feeChargesOverdue;
    private Integer penaltyChargesCharged;
    private Integer penaltyChargesPaid;
    private Integer penaltyChargesWaived;
    private Integer penaltyChargesWrittenOff;
    private Integer penaltyChargesOutstanding;
    private Integer penaltyChargesOverdue;
    private Double totalExpectedRepayment;
    private Double totalRepayment;
    private Double totalExpectedCostOfLoan;
    private Double totalCostOfLoan;
    private Integer totalWaived;
    private Integer totalWrittenOff;
    private Double totalOutstanding;
    private Double totalOverdue;
    private List<Integer> overdueSinceDate = new ArrayList<Integer>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    public Integer getInterestWaived() {
        return interestWaived;
    }

    public void setInterestWaived(Integer interestWaived) {
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

    public Integer getFeeChargesCharged() {
        return feeChargesCharged;
    }

    public void setFeeChargesCharged(Integer feeChargesCharged) {
        this.feeChargesCharged = feeChargesCharged;
    }

    public Integer getFeeChargesDueAtDisbursementCharged() {
        return feeChargesDueAtDisbursementCharged;
    }

    public void setFeeChargesDueAtDisbursementCharged(Integer feeChargesDueAtDisbursementCharged) {
        this.feeChargesDueAtDisbursementCharged = feeChargesDueAtDisbursementCharged;
    }

    public Integer getFeeChargesPaid() {
        return feeChargesPaid;
    }

    public void setFeeChargesPaid(Integer feeChargesPaid) {
        this.feeChargesPaid = feeChargesPaid;
    }

    public Integer getFeeChargesWaived() {
        return feeChargesWaived;
    }

    public void setFeeChargesWaived(Integer feeChargesWaived) {
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

    public Integer getFeeChargesOverdue() {
        return feeChargesOverdue;
    }

    public void setFeeChargesOverdue(Integer feeChargesOverdue) {
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

    public Integer getTotalWaived() {
        return totalWaived;
    }

    public void setTotalWaived(Integer totalWaived) {
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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

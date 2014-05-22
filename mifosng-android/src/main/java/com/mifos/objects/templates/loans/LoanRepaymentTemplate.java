
package com.mifos.objects.templates.loans;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class LoanRepaymentTemplate {

    @Expose
    private Type type;
    @Expose
    private List<Integer> date = new ArrayList<Integer>();
    @Expose
    private Currency currency;
    @Expose
    private Double amount;
    @Expose
    private Double principalPortion;
    @Expose
    private Double interestPortion;
    @Expose
    private Integer feeChargesPortion;
    @Expose
    private Integer penaltyChargesPortion;
    @Expose
    private List<PaymentTypeOption> paymentTypeOptions = new ArrayList<PaymentTypeOption>();

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Integer> getDate() {
        return date;
    }

    public void setDate(List<Integer> date) {
        this.date = date;
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

    public Double getPrincipalPortion() {
        return principalPortion;
    }

    public void setPrincipalPortion(Double principalPortion) {
        this.principalPortion = principalPortion;
    }

    public Double getInterestPortion() {
        return interestPortion;
    }

    public void setInterestPortion(Double interestPortion) {
        this.interestPortion = interestPortion;
    }

    public Integer getFeeChargesPortion() {
        return feeChargesPortion;
    }

    public void setFeeChargesPortion(Integer feeChargesPortion) {
        this.feeChargesPortion = feeChargesPortion;
    }

    public Integer getPenaltyChargesPortion() {
        return penaltyChargesPortion;
    }

    public void setPenaltyChargesPortion(Integer penaltyChargesPortion) {
        this.penaltyChargesPortion = penaltyChargesPortion;
    }

    public List<PaymentTypeOption> getPaymentTypeOptions() {
        return paymentTypeOptions;
    }

    public void setPaymentTypeOptions(List<PaymentTypeOption> paymentTypeOptions) {
        this.paymentTypeOptions = paymentTypeOptions;
    }

}

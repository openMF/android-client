
package com.mifos.objects.accounts.loan;

import com.mifos.objects.Currency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Transaction {

    private Integer id;
    private Integer officeId;
    private String officeName;
    private Type type;
    private List<Integer> date = new ArrayList<Integer>();
    private Currency currency;
    private PaymentDetailData paymentDetailData;
    private Double amount;
    private Double principalPortion;
    private Double interestPortion;
    private Integer feeChargesPortion;
    private Integer penaltyChargesPortion;
    private Integer overpaymentPortion;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

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

    public PaymentDetailData getPaymentDetailData() {
        return paymentDetailData;
    }

    public void setPaymentDetailData(PaymentDetailData paymentDetailData) {
        this.paymentDetailData = paymentDetailData;
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

    public Integer getOverpaymentPortion() {
        return overpaymentPortion;
    }

    public void setOverpaymentPortion(Integer overpaymentPortion) {
        this.overpaymentPortion = overpaymentPortion;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

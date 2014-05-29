
package com.mifos.objects.accounts.savings;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Charge {

    private Integer id;
    private Integer chargeId;
    private Integer accountId;
    private String name;
    private ChargeTimeType chargeTimeType;
    private ChargeCalculationType chargeCalculationType;
    private Integer percentage;
    private Integer amountPercentageAppliedTo;
    private Currency currency;
    private Double amount;
    private Double amountPaid;
    private Double amountWaived;
    private Double amountWrittenOff;
    private Double amountOutstanding;
    private Double amountOrPercentage;
    private Boolean penalty;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChargeId() {
        return chargeId;
    }

    public void setChargeId(Integer chargeId) {
        this.chargeId = chargeId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChargeTimeType getChargeTimeType() {
        return chargeTimeType;
    }

    public void setChargeTimeType(ChargeTimeType chargeTimeType) {
        this.chargeTimeType = chargeTimeType;
    }

    public ChargeCalculationType getChargeCalculationType() {
        return chargeCalculationType;
    }

    public void setChargeCalculationType(ChargeCalculationType chargeCalculationType) {
        this.chargeCalculationType = chargeCalculationType;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public Integer getAmountPercentageAppliedTo() {
        return amountPercentageAppliedTo;
    }

    public void setAmountPercentageAppliedTo(Integer amountPercentageAppliedTo) {
        this.amountPercentageAppliedTo = amountPercentageAppliedTo;
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

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Double getAmountWaived() {
        return amountWaived;
    }

    public void setAmountWaived(Double amountWaived) {
        this.amountWaived = amountWaived;
    }

    public Double getAmountWrittenOff() {
        return amountWrittenOff;
    }

    public void setAmountWrittenOff(Double amountWrittenOff) {
        this.amountWrittenOff = amountWrittenOff;
    }

    public Double getAmountOutstanding() {
        return amountOutstanding;
    }

    public void setAmountOutstanding(Double amountOutstanding) {
        this.amountOutstanding = amountOutstanding;
    }

    public Double getAmountOrPercentage() {
        return amountOrPercentage;
    }

    public void setAmountOrPercentage(Double amountOrPercentage) {
        this.amountOrPercentage = amountOrPercentage;
    }

    public Boolean getPenalty() {
        return penalty;
    }

    public void setPenalty(Boolean penalty) {
        this.penalty = penalty;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

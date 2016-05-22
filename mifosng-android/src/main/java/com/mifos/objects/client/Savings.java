package com.mifos.objects.client;

import com.mifos.objects.Currency;
import com.mifos.objects.accounts.savings.DepositType;
import com.mifos.objects.accounts.savings.Status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nellyk on 2/19/2016.
 */
public class Savings {
    private Integer id;
    private String accountNo;
    private Integer productId;
    private String productName;
    private Status status;
    private Currency currency;
    private Double accountBalance;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private DepositType depositType;

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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public DepositType getDepositType() {
        return depositType;
    }

    public void setDepositType(DepositType depositType) {
        this.depositType = depositType;
    }

    @Override
    public String toString() {
        return "SavingsAccount{" +
                "accountBalance=" + accountBalance +
                ", id=" + id +
                ", accountNo='" + accountNo + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", status=" + status +
                ", currency=" + currency +
                ", additionalProperties=" + additionalProperties +
                ", depositType=" + depositType +
                '}';
    }

}

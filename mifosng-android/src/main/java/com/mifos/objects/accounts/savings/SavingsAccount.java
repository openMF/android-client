/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.savings;

import com.mifos.objects.Currency;

import java.util.HashMap;
import java.util.Map;

public class SavingsAccount {

    private Integer id;
    private String accountNo;
    private Integer productId;
    private String productName;
    private Status status;
    private Currency currency;
    private Double accountBalance;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SavingsAccount withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public SavingsAccount withAccountNo(String accountNo) {
        this.accountNo = accountNo;
        return this;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public SavingsAccount withProductId(Integer productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public SavingsAccount withProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SavingsAccount withStatus(Status status) {
        this.status = status;
        return this;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public SavingsAccount withCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public SavingsAccount withAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
        return this;
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
                '}';
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

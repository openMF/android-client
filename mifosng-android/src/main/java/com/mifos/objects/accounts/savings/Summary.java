/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.savings;

import com.mifos.objects.Currency;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Summary {

    private Currency currency;
    private Double totalDeposits;
    private Double accountBalance;
    private Double totalWithdrawals;
    private Double totalInterestEarned;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getTotalDeposits() {
        return totalDeposits;
    }

    public void setTotalDeposits(Double totalDeposits) {
        this.totalDeposits = totalDeposits;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Double getTotalWithdrawals() {
        return totalWithdrawals;
    }

    public void setTotalWithdrawals(Double totalWithdrawals) {
        this.totalWithdrawals = totalWithdrawals;
    }

    public Double getTotalInterestEarned() {
        return totalInterestEarned;
    }

    public void setTotalInterestEarned(Double totalInterestEarned) {
        this.totalInterestEarned = totalInterestEarned;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.db;

import com.google.gson.Gson;

public class Loan {
    public double disbursementAmount;
    public double interestDue;
    public double interestPaid;
    public int loanId;
    public double chargesDue;
    public double totalDue;
    public double principalDue;
    public double principalPaid;
    public String accountId;
    public int accountStatusId;
    public String productShortName;
    public int productId;
    private Currency currency;
    private Client client;
    private String isPaymentChanged;

    public String getIsPaymentChanged() {
        return isPaymentChanged;
    }

    public void setIsPaymentChanged(String isPaymentChanged) {
        this.isPaymentChanged = isPaymentChanged;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public double getDisbursementAmount() {
        return disbursementAmount;
    }

    public void setDisbursementAmount(int disbursementAmount) {
        this.disbursementAmount = disbursementAmount;
    }

    public double getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(double totalDue) {
        this.totalDue = totalDue;
    }

    public int getAccountStatusId() {
        return accountStatusId;
    }

    public String getProductShortName() {
        return productShortName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Loan loan = (Loan) o;

        if (loanId != loan.loanId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return loanId;
    }
}

package com.mifos.objects.db;

import com.google.gson.Gson;
import com.orm.SugarRecord;

public class Loan extends SugarRecord<Loan> {
    public int disbursementAmount;
    public int interestDue;
    public int interestPaid;
    public int loanId;
    public int chargesDue;
    public int totalDue;
    public int principalDue;
    public int principalPaid;
    public String accountId;
    public int accountStatusId;
    public String productShortName;
    public int productId;
    private Currency currency;
    private Client client;

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

    public int getDisbursementAmount() {
        return disbursementAmount;
    }

    public void setDisbursementAmount(int disbursementAmount) {
        this.disbursementAmount = disbursementAmount;
    }

}

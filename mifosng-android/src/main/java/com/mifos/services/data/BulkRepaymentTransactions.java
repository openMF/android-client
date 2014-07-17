package com.mifos.services.data;

import com.google.gson.Gson;

public class BulkRepaymentTransactions
{
    public int loanId;
    public double transactionAmount;

    public BulkRepaymentTransactions() {
    }

    public BulkRepaymentTransactions(int loanId, double transactionAmount) {
        this.loanId = loanId;
        this.transactionAmount = transactionAmount;
    }

    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }

}

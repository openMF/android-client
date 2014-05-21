package com.mifos.services.data;

import com.google.gson.Gson;

public class BulkRepaymentTransactions
{
    public int loanId;
    public int transactionAmount;

    public BulkRepaymentTransactions() {
    }

    public BulkRepaymentTransactions(int loanId, int transactionAmount) {
        this.loanId = loanId;
        this.transactionAmount = transactionAmount;
    }

    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }

}

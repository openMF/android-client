/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api.model;

import com.google.gson.Gson;

public class BulkRepaymentTransactions {
    public int loanId;
    public double transactionAmount;

    public BulkRepaymentTransactions() {
    }

    public BulkRepaymentTransactions(int loanId, double transactionAmount) {
        this.loanId = loanId;
        this.transactionAmount = transactionAmount;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}

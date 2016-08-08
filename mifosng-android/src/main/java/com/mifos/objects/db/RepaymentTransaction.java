/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.db;

public class RepaymentTransaction {

    private Loan loan;
    private double transactionAmount;

    public RepaymentTransaction() {

    }

    public RepaymentTransaction(Loan loan, double transactionAmount) {
        this.loan = loan;
        this.transactionAmount = transactionAmount;
    }

    public Loan getLoan() {
        return loan;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}

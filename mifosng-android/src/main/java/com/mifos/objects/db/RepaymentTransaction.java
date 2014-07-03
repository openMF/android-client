package com.mifos.objects.db;

import com.orm.SugarRecord;

public class RepaymentTransaction extends SugarRecord<RepaymentTransaction> {

    private Loan loan;
    private int transactionAmount;

    public RepaymentTransaction(){

    }

    public RepaymentTransaction(Loan loan, int transactionAmount) {
        this.loan = loan;
        this.transactionAmount = transactionAmount;
    }

    public Loan getLoan() {
        return loan;
    }

    public int getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(int transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}

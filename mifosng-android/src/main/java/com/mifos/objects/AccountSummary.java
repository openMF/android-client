package com.mifos.objects;

/**
 * Created by Tarun on 13/03/2016.
 */
public class AccountSummary {
    private int loanCycle;
    private int activeLoans;
    private float lastLoanAmount;
    private int activeSavings;
    private float totalSavings;

    public int getLoanCycle() {
        return loanCycle;
    }

    public int getActiveLoans() {
        return activeLoans;
    }

    public float getLastLoanAmount() {
        return lastLoanAmount;
    }

    public int getActiveSavings() {
        return activeSavings;
    }

    public float getTotalSavings() {
        return totalSavings;
    }
}


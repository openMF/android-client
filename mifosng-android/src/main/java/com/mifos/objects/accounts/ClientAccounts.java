
/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts;

import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.savings.SavingsAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientAccounts {

    private List<LoanAccount> loanAccounts = new ArrayList<LoanAccount>();
    private List<SavingsAccount> savingsAccounts = new ArrayList<SavingsAccount>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public boolean isNullOrEmpty() {
        return((loanAccounts == null) || loanAccounts.isEmpty());
    }

    public List<LoanAccount> getLoanAccounts() {
        return loanAccounts;
    }

    public void setLoanAccounts(List<LoanAccount> loanAccounts) {
        this.loanAccounts = loanAccounts;
    }

    public ClientAccounts withLoanAccounts(List<LoanAccount> loanAccounts) {
        this.loanAccounts = loanAccounts;
        return this;
    }

    public List<SavingsAccount> getSavingsAccounts() {
        return savingsAccounts;
    }

    public void setSavingsAccounts(List<SavingsAccount> savingsAccounts) {
        this.savingsAccounts = savingsAccounts;
    }

    public ClientAccounts withSavingsAccounts(List<SavingsAccount> savingsAccounts) {
        this.savingsAccounts = savingsAccounts;
        return this;
    }

    public List<SavingsAccount>getRecurringSavingsAccounts() {
        return getSavingsAccounts(true);
    }

    public List<SavingsAccount>getNonRecurringSavingsAccounts() {
        return getSavingsAccounts(false);
    }

    private List<SavingsAccount>getSavingsAccounts(boolean wantRecurring) {
        List<SavingsAccount> result = new ArrayList<SavingsAccount>();
        if (this.savingsAccounts != null) {
            for (SavingsAccount account : savingsAccounts) {
                if (account.isRecurring() == wantRecurring) {
                    result.add(account);
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "ClientAccounts{" +
                "loanAccounts=" + loanAccounts +
                ", savingsAccounts=" + savingsAccounts +
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

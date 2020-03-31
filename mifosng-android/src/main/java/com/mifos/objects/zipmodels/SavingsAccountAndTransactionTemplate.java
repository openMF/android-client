package com.mifos.objects.zipmodels;

import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;

/**
 * Created by Rajan Maurya on 21/08/16.
 */
public class SavingsAccountAndTransactionTemplate {

    SavingsAccountWithAssociations savingsAccountWithAssociations;
    SavingsAccountTransactionTemplate savingsAccountTransactionTemplate;

    public SavingsAccountWithAssociations getSavingsAccountWithAssociations() {
        return savingsAccountWithAssociations;
    }

    public void setSavingsAccountWithAssociations(SavingsAccountWithAssociations
                                                          savingsAccountWithAssociations) {
        this.savingsAccountWithAssociations = savingsAccountWithAssociations;
    }

    public SavingsAccountTransactionTemplate getSavingsAccountTransactionTemplate() {
        return savingsAccountTransactionTemplate;
    }

    public void setSavingsAccountTransactionTemplate(SavingsAccountTransactionTemplate
                                                             savingsAccountTransactionTemplate) {
        this.savingsAccountTransactionTemplate = savingsAccountTransactionTemplate;
    }

    public SavingsAccountAndTransactionTemplate() {

    }

    public SavingsAccountAndTransactionTemplate(SavingsAccountWithAssociations
                                                        savingsAccountWithAssociations,
                                                SavingsAccountTransactionTemplate
                                                        savingsAccountTransactionTemplate) {
        this.savingsAccountWithAssociations = savingsAccountWithAssociations;
        this.savingsAccountTransactionTemplate = savingsAccountTransactionTemplate;
    }

    @Override
    public String toString() {
        return "SavingsAccountAndTransactionTemplate{" +
                "savingsAccountWithAssociations=" + savingsAccountWithAssociations +
                ", savingsAccountTransactionTemplate=" + savingsAccountTransactionTemplate +
                '}';
    }
}

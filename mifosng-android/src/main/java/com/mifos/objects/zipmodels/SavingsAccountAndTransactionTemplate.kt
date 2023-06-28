package com.mifos.objects.zipmodels

import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate

/**
 * Created by Rajan Maurya on 21/08/16.
 */
class SavingsAccountAndTransactionTemplate {
    var savingsAccountWithAssociations: SavingsAccountWithAssociations? = null
    var savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate? = null

    constructor() {}
    constructor(
        savingsAccountWithAssociations: SavingsAccountWithAssociations?,
        savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate?
    ) {
        this.savingsAccountWithAssociations = savingsAccountWithAssociations
        this.savingsAccountTransactionTemplate = savingsAccountTransactionTemplate
    }

    override fun toString(): String {
        return "SavingsAccountAndTransactionTemplate{" +
                "savingsAccountWithAssociations=" + savingsAccountWithAssociations +
                ", savingsAccountTransactionTemplate=" + savingsAccountTransactionTemplate +
                '}'
    }
}
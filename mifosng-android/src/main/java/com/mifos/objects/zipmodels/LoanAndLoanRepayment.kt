package com.mifos.objects.zipmodels

import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.objects.templates.loans.LoanRepaymentTemplate

/**
 *
 * Created by Rajan Maurya on 08/08/16.
 */
class LoanAndLoanRepayment {
    var loanWithAssociations: LoanWithAssociations? = null
    var loanRepaymentTemplate: LoanRepaymentTemplate? = null

    constructor() {}
    constructor(
        loanWithAssociations: LoanWithAssociations?,
        loanRepaymentTemplate: LoanRepaymentTemplate?
    ) {
        this.loanWithAssociations = loanWithAssociations
        this.loanRepaymentTemplate = loanRepaymentTemplate
    }

    override fun toString(): String {
        return "LoanAndLoanRepayment{" +
                "loanWithAssociations=" + loanWithAssociations +
                ", loanRepaymentTemplate=" + loanRepaymentTemplate +
                '}'
    }
}
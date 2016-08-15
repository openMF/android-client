package com.mifos.objects.zipmodels;

import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;

/**
 *
 * Created by Rajan Maurya on 08/08/16.
 */
public class LoanAndLoanRepayment {

    LoanWithAssociations loanWithAssociations;
    LoanRepaymentTemplate loanRepaymentTemplate;

    public LoanWithAssociations getLoanWithAssociations() {
        return loanWithAssociations;
    }

    public void setLoanWithAssociations(LoanWithAssociations loanWithAssociations) {
        this.loanWithAssociations = loanWithAssociations;
    }

    public LoanRepaymentTemplate getLoanRepaymentTemplate() {
        return loanRepaymentTemplate;
    }

    public void setLoanRepaymentTemplate(LoanRepaymentTemplate loanRepaymentTemplate) {
        this.loanRepaymentTemplate = loanRepaymentTemplate;
    }

    @Override
    public String toString() {
        return "LoanAndLoanRepayment{" +
                "loanWithAssociations=" + loanWithAssociations +
                ", loanRepaymentTemplate=" + loanRepaymentTemplate +
                '}';
    }
}
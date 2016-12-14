package com.mifos.mifosxdroid.dialogfragments.loanaccountdisbursement;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.templates.loans.LoanDisburseTemplate;


/**
 * Created by Rajan Maurya on 8/6/16.
 */
public interface LoanAccountDisbursementMvpView extends MvpView {

    void showLoanTemplate(LoanDisburseTemplate loanDisburseTemplate);

    void showDispurseLoanSuccessfully(Loans loans);

    void showError(String s);
}

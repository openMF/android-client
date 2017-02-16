package com.mifos.mifosxdroid.online.loanaccountdisbursement;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.templates.loans.LoanTransactionTemplate;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public interface LoanAccountDisbursementMvpView extends MvpView {

    void showUserInterface();

    void showDisbursementDate(String date);

    void showLoanTransactionTemplate(LoanTransactionTemplate loanTransactionTemplate);

    void showDisburseLoanSuccessfully(GenericResponse genericResponse);

    void showError(String s);

    void showError(int errorMessage);
}

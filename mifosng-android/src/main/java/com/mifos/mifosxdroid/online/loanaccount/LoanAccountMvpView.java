package com.mifos.mifosxdroid.online.loanaccount;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.LoanProducts;
import com.mifos.objects.templates.loans.LoanTemplate;

import java.util.List;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface LoanAccountMvpView extends MvpView {

    void showAllLoan(List<LoanProducts> productLoanses);

    void showLoanAccountTemplate(LoanTemplate loanTemplate);

    void showLoanAccountCreatedSuccessfully(Loans loans);

    void showMessage(int messageId);

    void showFetchingError(String s);
}

package com.mifos.mifosxdroid.online.grouploanaccount;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.LoanProducts;
import com.mifos.objects.templates.loans.GroupLoanTemplate;

import java.util.List;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface GroupLoanAccountMvpView extends MvpView {

    void showAllLoans(List<LoanProducts> productLoans);

    void showGroupLoansAccountCreatedSuccessfully(Loans loans);

    void showGroupLoanTemplate(GroupLoanTemplate groupLoanTemplate);

    void showFetchingError(String s);
}

package com.mifos.mifosxdroid.online.grouploanaccount;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.LoanProducts;

import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface GroupLoanAccountMvpView extends MvpView {

    void showAllLoans(List<LoanProducts> productLoans);

    void showLoanPurposeSpinner(ResponseBody response);

    void showGroupLoansAccountCreatedSuccessfully(Loans loans);

    void showFetchingError(String s);
}

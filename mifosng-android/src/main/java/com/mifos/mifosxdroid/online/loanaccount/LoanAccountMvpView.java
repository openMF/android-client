package com.mifos.mifosxdroid.online.loanaccount;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.ProductLoans;

import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface LoanAccountMvpView extends MvpView {

    void showAllLoan(List<ProductLoans> productLoanses);

    void showLoanAccountTemplate(ResponseBody response);

    void showLoanAccountCreatedSuccessfully(Loans loans);

    void showFetchingError(String s);
}

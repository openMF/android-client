package com.mifos.mifosxdroid.online.loanaccount;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.ProductLoans;
import com.mifos.services.data.LoansPayload;

import java.util.List;

import retrofit.client.Response;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface LoanAccountMvpView extends MvpView {

    void showAllLoan(List<ProductLoans> productLoanses);

    void showLoanAccountTemplate(Response response);

    void showLoanAccountCreatedSuccessfully(Loans loans);

    void showFetchingError(String s);
}

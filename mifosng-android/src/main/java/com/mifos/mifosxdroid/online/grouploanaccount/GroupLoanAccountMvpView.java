package com.mifos.mifosxdroid.online.grouploanaccount;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.ProductLoans;
import com.mifos.services.data.GroupLoanPayload;

import java.util.List;

import retrofit.client.Response;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface GroupLoanAccountMvpView extends MvpView {

    void showAllLoans(List<ProductLoans> productLoans);

    void showLoanPurposeSpinner(Response response);

    void showGroupLoansAccountCreatedSuccessfully(Loans loans);

    void showFetchingError(String s);
}

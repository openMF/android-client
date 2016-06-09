package com.mifos.mifosxdroid.dialogfragments.loanaccountdisbursement;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;

import retrofit.client.Response;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public interface LoanAccountDisbursementMvpView extends MvpView {

    void showLoanTemplate(Response response);

    void showDispurseLoanSuccessfully(GenericResponse genericResponse);

    void showError(String s);
}

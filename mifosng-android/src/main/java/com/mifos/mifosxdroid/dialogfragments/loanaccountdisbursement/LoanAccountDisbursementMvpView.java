package com.mifos.mifosxdroid.dialogfragments.loanaccountdisbursement;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;

import okhttp3.ResponseBody;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public interface LoanAccountDisbursementMvpView extends MvpView {

    void showLoanTemplate(ResponseBody response);

    void showDispurseLoanSuccessfully(GenericResponse genericResponse);

    void showError(String s);
}

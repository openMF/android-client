package com.mifos.mifosxdroid.dialogfragments.loanaccountapproval;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public interface LoanAccountApprovalMvpView extends MvpView {

    void showLoanApproveSuccessfully(GenericResponse genericResponse);

    void showLoanApproveFailed(String s);
}

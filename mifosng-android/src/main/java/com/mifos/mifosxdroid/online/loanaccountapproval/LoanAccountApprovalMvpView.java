package com.mifos.mifosxdroid.online.loanaccountapproval;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public interface LoanAccountApprovalMvpView extends MvpView {

    void showUserInterface();

    void showLoanApproveSuccessfully(GenericResponse genericResponse);

    void showLoanApproveFailed(String s);
}

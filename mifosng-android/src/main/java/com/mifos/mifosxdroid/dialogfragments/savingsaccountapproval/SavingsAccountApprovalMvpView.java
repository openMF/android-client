package com.mifos.mifosxdroid.dialogfragments.savingsaccountapproval;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 09/06/16.
 */
public interface SavingsAccountApprovalMvpView extends MvpView {

    void showSavingAccountApprovedSuccessfully(GenericResponse genericResponse);

    void showError(String s);
}

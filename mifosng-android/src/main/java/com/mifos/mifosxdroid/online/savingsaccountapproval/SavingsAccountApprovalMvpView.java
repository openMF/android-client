package com.mifos.mifosxdroid.online.savingsaccountapproval;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 09/06/16.
 */
public interface SavingsAccountApprovalMvpView extends MvpView {

    void showUserInterface();

    void showSavingAccountApprovedSuccessfully(GenericResponse genericResponse);

    void showError(String s);
}

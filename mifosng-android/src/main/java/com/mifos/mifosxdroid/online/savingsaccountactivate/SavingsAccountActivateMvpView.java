package com.mifos.mifosxdroid.online.savingsaccountactivate;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Tarun on 01/06/17.
 */
public interface SavingsAccountActivateMvpView extends MvpView {

    void showUserInterface();

    void showSavingAccountActivatedSuccessfully(GenericResponse genericResponse);

    void showError(String s);
}

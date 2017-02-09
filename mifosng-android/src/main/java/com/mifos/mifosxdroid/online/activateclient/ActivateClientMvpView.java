package com.mifos.mifosxdroid.online.activateclient;

import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 09/02/17.
 */

public interface ActivateClientMvpView extends MvpView {

    void showUserInterface();

    void showClientActivatedSuccessfully();

    void showError(String errorMessage);
}

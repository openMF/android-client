package com.mifos.mifosxdroid.online.activate;

import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 09/02/17.
 */

public interface ActivateMvpView extends MvpView {

    void showUserInterface();

    void showActivatedSuccessfully(int message);

    void showError(String errorMessage);
}

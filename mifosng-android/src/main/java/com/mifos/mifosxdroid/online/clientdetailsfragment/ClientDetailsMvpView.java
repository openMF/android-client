package com.mifos.mifosxdroid.online.clientdetailsfragment;

import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 17/3/16.
 */
public interface ClientDetailsMvpView extends MvpView {

    void showClientDetailsProgressBar(boolean status);

    void showSuccessfullRequest(String done);

    void showError(String error);

}

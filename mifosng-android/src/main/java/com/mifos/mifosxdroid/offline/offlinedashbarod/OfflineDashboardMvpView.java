package com.mifos.mifosxdroid.offline.offlinedashbarod;

import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 20/07/16.
 */
public interface OfflineDashboardMvpView extends MvpView {

    void showClients();

    void showGroups();

    void showError();
}

package com.mifos.mifosxdroid.offline.syncgrouppayloads;

import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 19/07/16.
 */
public interface SyncGroupPayloadsMvpView extends MvpView {

    void showGroupSyncResponse();

    void showGroupSyncFailed();
}

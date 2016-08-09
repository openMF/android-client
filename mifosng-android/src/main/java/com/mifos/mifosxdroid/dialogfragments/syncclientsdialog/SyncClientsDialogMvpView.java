package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Client;

/**
 * Created by Rajan Maurya on 08/08/16.
 */
public interface SyncClientsDialogMvpView extends MvpView {

    void showClientSyncedSuccessfully(Client client);

    void showError(int s);

}

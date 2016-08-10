package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.ClientAccounts;

/**
 * Created by Rajan Maurya on 08/08/16.
 */
public interface SyncClientsDialogMvpView extends MvpView {

    void syncClientInformation();

    void showClientAccountsSyncedSuccessfully(ClientAccounts clientAccounts);

    void showLoanSyncSuccessfully();

    void showLoanRepaymentSyncSuccessfully();

    void showClientSyncSuccessfully();

    void showError(int s);

}

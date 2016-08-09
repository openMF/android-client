package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.client.Client;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;

/**
 * Created by Rajan Maurya on 08/08/16.
 */
public interface SyncClientsDialogMvpView extends MvpView {

    void showClientAndAccountsSyncedSuccessfully(Client client, ClientAccounts clientAccounts);

    void showLoanSyncSuccessfully(LoanWithAssociations loanWithAssociations);

    void showLoanRepayentSyncSuccessfully(LoanRepaymentTemplate loanRepaymentTemplate);

    void showError(int s);

}

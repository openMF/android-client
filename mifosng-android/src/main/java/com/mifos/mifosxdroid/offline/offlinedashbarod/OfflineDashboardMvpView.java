package com.mifos.mifosxdroid.offline.offlinedashbarod;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.client.ClientPayload;
import com.mifos.objects.group.GroupPayload;

import java.util.List;

/**
 * Created by Rajan Maurya on 20/07/16.
 */
public interface OfflineDashboardMvpView extends MvpView {

    void showClients(List<ClientPayload> clientPayloads);

    void showGroups(List<GroupPayload> groupPayloads);

    void showLoanRepaymentTransactions(List<LoanRepaymentRequest> loanRepaymentRequests);

    void showNoPayloadToShow();

    void showError(int s);
}

package com.mifos.mifosxdroid.offline.offlinedashbarod;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.client.ClientPayload;
import com.mifos.objects.group.GroupPayload;
import com.mifos.services.data.CenterPayload;

import java.util.List;

/**
 * Created by Rajan Maurya on 20/07/16.
 */
public interface OfflineDashboardMvpView extends MvpView {

    void showClients(List<ClientPayload> clientPayloads);

    void showGroups(List<GroupPayload> groupPayloads);

    void showCenters(List<CenterPayload> centerPayloads);

    void showLoanRepaymentTransactions(List<LoanRepaymentRequest> loanRepaymentRequests);

    void showSavingsAccountTransaction(List<SavingsAccountTransactionRequest> transactions);

    void showNoPayloadToShow();

    void showError(int s);
}

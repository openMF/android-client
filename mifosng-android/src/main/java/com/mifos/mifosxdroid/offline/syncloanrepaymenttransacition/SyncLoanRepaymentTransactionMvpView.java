package com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;

import java.util.List;

/**
 * Created by Rajan Maurya on 28/07/16.
 */
public interface SyncLoanRepaymentTransactionMvpView extends MvpView {

    void showLoanRepaymentTransactions(List<LoanRepaymentRequest> loanRepaymentRequests);

    void showPaymentTypeOption(List<PaymentTypeOption> paymentTypeOptions);

    void showOfflineModeDialog();

    void showPaymentSubmittedSuccessfully();

    void showPaymentFailed(String errorMessage);

    void showLoanRepaymentUpdated(LoanRepaymentRequest loanRepaymentRequest);

    void showLoanRepaymentDeletedAndUpdateLoanRepayment(
            List<LoanRepaymentRequest> loanRepaymentRequests);

    void showError(int stringId);
}

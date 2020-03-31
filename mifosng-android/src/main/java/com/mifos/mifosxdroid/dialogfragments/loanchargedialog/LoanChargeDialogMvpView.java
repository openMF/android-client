package com.mifos.mifosxdroid.dialogfragments.loanchargedialog;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.ChargeCreationResponse;

import okhttp3.ResponseBody;

/**
 * Created by Rajan Maurya on 9/6/16.
 */
public interface LoanChargeDialogMvpView extends MvpView {

    void showAllChargesV3(ResponseBody response);

    void showLoanChargesCreatedSuccessfully(ChargeCreationResponse chargeCreationResponse);

    void showChargeCreatedFailure(String errorMessage);

    void showError(String errorMessage);
}

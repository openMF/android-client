package com.mifos.mifosxdroid.dialogfragments.loanchargedialog;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Charges;

import okhttp3.ResponseBody;

/**
 * Created by Rajan Maurya on 9/6/16.
 */
public interface LoanChargeDialogMvpView extends MvpView {

    void showAllChargesV3(ResponseBody response);

    void showLoanChargesCreatedSuccessfully(Charges charges);

    void showError(String s);
}

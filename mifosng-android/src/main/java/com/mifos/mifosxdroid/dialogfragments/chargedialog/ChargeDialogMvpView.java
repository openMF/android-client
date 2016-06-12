package com.mifos.mifosxdroid.dialogfragments.chargedialog;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Charges;

import okhttp3.ResponseBody;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface ChargeDialogMvpView extends MvpView {

    void showAllChargesV2(ResponseBody response);

    void showChargesCreatedSuccessfully(Charges changes);

    void showFetchingError(String s);
}

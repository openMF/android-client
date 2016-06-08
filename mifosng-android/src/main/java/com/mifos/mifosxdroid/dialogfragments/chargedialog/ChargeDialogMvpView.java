package com.mifos.mifosxdroid.dialogfragments.chargedialog;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Charges;

import retrofit.client.Response;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface ChargeDialogMvpView extends MvpView {

    void showAllChargesV2(Response response);

    void showChargesCreatedSuccessfully(Charges changes);

    void showFetchingError(String s);
}

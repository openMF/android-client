package com.mifos.mifosxdroid.dialogfragments.chargedialog;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.ChargeCreationResponse;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface ChargeDialogMvpView extends MvpView {

    void showAllChargesV2(Page<Charges> chargesPage);

    void showChargesCreatedSuccessfully(ChargeCreationResponse chargeCreationResponse);

    void showChargeCreatedFailure(String errorMessage);

    void showFetchingError(String s);
}

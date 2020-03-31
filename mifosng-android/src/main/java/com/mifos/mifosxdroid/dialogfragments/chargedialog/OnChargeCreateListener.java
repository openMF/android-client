package com.mifos.mifosxdroid.dialogfragments.chargedialog;

import com.mifos.objects.client.Charges;

/**
 * Created by Tarun on 13-08-17.
 */

public interface OnChargeCreateListener {

    void onChargeCreatedSuccess(Charges charge);

    void onChargeCreatedFailure(String errorMessage);
}

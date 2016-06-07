package com.mifos.mifosxdroid.online.clientcharge;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;

import retrofit.client.Response;

/**
 * Created by Rajan Maurya on 5/6/16.
 */
public interface ClientChargeMvpView extends MvpView {

    void showChargesList(Page<Charges> chargesPage);

    void showFetchingErrorCharges(Response response);

    void showMoreClientCharges(Page<Charges> chargesPage);
}

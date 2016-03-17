package com.mifos.mifosxdroid.online.clientchargefragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;

/**
 * Created by Rajan Maurya on 17/3/16.
 */
public interface ClientChargeMvpView extends MvpView {

    void showClientChargesProgressBar(boolean status);

    void showClientChargesList(Page<Charges> chargesPage);

    void showClientChargesListFetchError();

    void showMoreClientChargesList(Page<Charges> chargesPage);
}

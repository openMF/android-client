package com.mifos.mifosxdroid.online.loancharge;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public interface LoanChargeMvpView extends MvpView {

    void showLoanChargesList(Page<Charges> chargesPage);

    void showChargesList(Page<Charges> chargesPage);

    void showFetchingError(int response);
}

package com.mifos.mifosxdroid.online.loancharge;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Charges;

import java.util.List;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public interface LoanChargeMvpView extends MvpView {

    void showLoanChargesList(List<Charges> charges);

    void showFetchingError(String s);
}

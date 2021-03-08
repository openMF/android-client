package com.mifos.mifosxdroid.online.loancharge

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.client.Charges

/**
 * Created by Rajan Maurya on 07/06/16.
 */
interface LoanChargeMvpView : MvpView {
    fun showLoanChargesList(charges: MutableList<Charges>)
    fun showFetchingError(s: String)
}
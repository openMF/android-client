package com.mifos.mifosxdroid.online.clientcharge

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.client.Charges
import com.mifos.objects.client.Page

/**
 * Created by Rajan Maurya on 5/6/16.
 */
interface ClientChargeMvpView : MvpView {
    fun showChargesList(chargesPage: Page<Charges?>?)
    fun showFetchingErrorCharges(s: String)
    fun showEmptyCharges()
}
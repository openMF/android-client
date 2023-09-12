package com.mifos.mifosxdroid.online.clientcharge

import com.mifos.objects.client.Charges
import com.mifos.objects.client.Page
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface ClientChargeRepository {

    fun getClientCharges(clientId: Int, offset: Int, limit: Int): Observable<Page<Charges>>
}
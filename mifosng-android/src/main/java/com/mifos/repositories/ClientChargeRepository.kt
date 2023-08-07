package com.mifos.repositories

import com.mifos.objects.client.Charges
import com.mifos.objects.client.Page
import rx.Observable

interface ClientChargeRepository {

    fun getClientCharges(clientId: Int, offset: Int, limit: Int): Observable<Page<Charges>>
}
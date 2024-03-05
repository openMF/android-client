package com.mifos.mifosxdroid.online.clientcharge

import com.mifos.core.network.datamanager.DataManagerCharge
import com.mifos.core.objects.client.Charges
import com.mifos.core.objects.client.Page
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class ClientChargeRepositoryImp @Inject constructor(private val dataManagerCharge: DataManagerCharge) :
    ClientChargeRepository {
    override fun getClientCharges(
        clientId: Int,
        offset: Int,
        limit: Int
    ): Observable<Page<Charges>> {
        return dataManagerCharge.getClientCharges(clientId, offset, limit)
    }
}
package com.mifos.core.network.datamanager

import com.mifos.core.databasehelper.DatabaseHelperCharge
import com.mifos.core.datastore.PrefManager
import com.mifos.core.network.BaseApiManager
import com.mifos.core.objects.client.Charges
import com.mifos.core.objects.client.Page
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This DataManager is for Managing Charge API, In which Request is going to Server
 * and In Response, We are getting Charge API Observable Response using Retrofit2.
 * DataManagerCharge saving response in Database and response to Presenter as accordingly.
 *
 *
 * Created by Rajan Maurya on 4/7/16.
 */
@Singleton
class DataManagerCharge @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    val mDatabaseHelperCharge: DatabaseHelperCharge,
    private val prefManager: PrefManager
) {
    /**
     * This Method Request the Charge API at
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/charges
     * and in response get the of the Charge Page that contains Charges list.
     *
     * @param clientId Client Id
     * @param offset   Offset From Which Position Charge List user want
     * @param limit    Maximum Limit of the Response Charge List Size
     * @return Page<Charge> Page of Charge in Which List Size is according to Limit and from
     * where position is Starting according to offset</Charge>>
     */
    fun getClientCharges(clientId: Int, offset: Int, limit: Int): Observable<Page<Charges>> {
        return when (prefManager.userStatus) {
            false -> mBaseApiManager.chargeApi.getListOfCharges(clientId, offset, limit)
                .concatMap { chargesPage ->
                    mDatabaseHelperCharge.saveClientCharges(chargesPage, clientId)
                    Observable.just(chargesPage)
                }

            true -> {
                /**
                 * Return Client Charges from DatabaseHelperClient only one time.
                 */
                if (offset == 0) mDatabaseHelperCharge.readClientCharges(clientId) else Observable.just(
                    Page()
                )
            }
        }
    }
}
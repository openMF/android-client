package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.api.local.databasehelper.DatabaseHelperCharge
import com.mifos.objects.client.Charges
import com.mifos.objects.client.Page
import com.mifos.utils.PrefManager
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This DataManager is for Managing Center API, In which Request is going to Server
 * and In Response, We are getting Center API Observable Response using Retrofit2.
 * DataManagerCenter saving response in Database and response to Presenter as accordingly.
 * Created by Atharv Tare on 23/03/23.
 */
@Singleton
class DataManagerCharge @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    val mDatabaseHelperCharge: DatabaseHelperCharge
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
        return when (PrefManager.getUserStatus()) {
            0 -> mBaseApiManager.chargeApi.getListOfCharges(clientId, offset, limit)
                .concatMap { chargesPage ->
                    mDatabaseHelperCharge.saveClientCharges(chargesPage, clientId)
                    Observable.just(
                        chargesPage
                    )
                }
            1 -> {
                /**
                 * Return Client Charges from DatabaseHelperClient only one time.
                 */
                if (offset == 0) mDatabaseHelperCharge.readClientCharges(clientId) else Observable.just(
                    Page()
                )
            }
            else -> Observable.just(Page())
        }
    }
}
